<?php

namespace App\Command;

use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Style\SymfonyStyle;
use Symfony\Contracts\HttpClient\HttpClientInterface;

#[AsCommand(
    name: 'app:fetch-config',
    description: 'Récupère la configuration depuis Spring Cloud Config Server'
)]
class FetchConfigCommand extends Command
{
    public function __construct(
        private ?HttpClientInterface $httpClient = null
    ) {
        parent::__construct();
    }

    protected function configure(): void
    {
        $this
            ->addOption('server', 's', InputOption::VALUE_OPTIONAL, 'URL du Config Server', 'http://localhost:8888')
            ->addOption('app', 'a', InputOption::VALUE_OPTIONAL, 'Nom de l\'application', 'module5-support-client')
            ->addOption('profile', 'p', InputOption::VALUE_OPTIONAL, 'Profil actif', 'default')
            ->addOption('apply', null, InputOption::VALUE_NONE, 'Appliquer les variables dans le fichier .env.local');
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $io = new SymfonyStyle($input, $output);

        $server = rtrim((string) $input->getOption('server'), '/');
        $appName = (string) $input->getOption('app');
        $profile = (string) $input->getOption('profile');
        $url = sprintf('%s/%s/%s', $server, $appName, $profile);

        $io->title('Interrogation du Config Server');
        $io->text(sprintf('URL: <info>%s</info>', $url));

        try {
            $jsonData = null;
            if ($this->httpClient !== null) {
                $response = $this->httpClient->request('GET', $url, [
                    'timeout' => 5,
                    'headers' => ['Accept' => 'application/json'],
                ]);
                if ($response->getStatusCode() === 200) {
                    $jsonData = $response->toArray();
                }
            } else {
                $context = stream_context_create([
                    'http' => ['timeout' => 5, 'header' => "Accept: application/json\r\n"]
                ]);
                $content = @file_get_contents($url, false, $context);
                if ($content !== false) {
                    $jsonData = json_decode($content, true);
                }
            }

            if (!$jsonData || !isset($jsonData['propertySources'])) {
                $io->error('Impossible de récupérer la configuration depuis le Config Server. Vérifiez qu\'il est bien démarré sur ' . $server);
                return Command::FAILURE;
            }

            $allProperties = [];
            foreach ($jsonData['propertySources'] as $source) {
                if (isset($source['source']) && is_array($source['source'])) {
                    foreach ($source['source'] as $key => $value) {
                        $allProperties[$key] = $value;
                    }
                }
            }

            $io->success(sprintf('Configuration reçue (%d propriétés trouvées)', count($allProperties)));

            $tableRows = [];
            foreach ($allProperties as $key => $value) {
                $tableRows[] = [$key, is_scalar($value) ? (string) $value : json_encode($value)];
            }
            $io->table(['Propriété', 'Valeur'], $tableRows);

            if ($input->getOption('apply')) {
                $envLocalPath = dirname(__DIR__, 2) . '/.env.local';
                $envLines = [
                    '# Fichier auto-généré depuis Config Server ' . date('Y-m-d H:i:s'),
                ];
                foreach ($allProperties as $key => $val) {
                    $normalizedKey = strtoupper(str_replace(['.', '-'], '_', $key));
                    $envLines[] = sprintf('%s="%s"', $normalizedKey, addslashes((string) $val));
                }
                file_put_contents($envLocalPath, implode(PHP_EOL, $envLines) . PHP_EOL);
                $io->success('Les variables ont été enregistrées dans .env.local');
            } else {
                $io->note('Pour appliquer ces propriétés dans .env.local, ajoutez l\'option --apply');
            }

            return Command::SUCCESS;
        } catch (\Throwable $e) {
            $io->error('Erreur lors de la communication avec le Config Server: ' . $e->getMessage());
            return Command::FAILURE;
        }
    }
}
