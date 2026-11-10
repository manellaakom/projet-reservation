<?php

namespace App\Command;

use Eureka\EurekaClient;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

#[AsCommand(
    name: 'app:eureka:register',
    description: 'Enregistre le module sur Eureka',
)]
class EurekaRegisterCommand extends Command
{
    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $output->writeln('Connexion au serveur Eureka...');

        $eurekaUrl = $_ENV['EUREKA_SERVER_URL'] ?? 'http://host.docker.internal:8761/eureka';
        if (str_contains($eurekaUrl, 'localhost')) {
            $eurekaUrl = str_replace('localhost', 'host.docker.internal', $eurekaUrl);
        }
        $appName   = $_ENV['EUREKA_APP_NAME'] ?? 'SUPPORT-CLIENT-BACKEND';
        $hostName  = $_ENV['EUREKA_HOST'] ?? 'localhost';
        $port      = (int)($_ENV['EUREKA_PORT'] ?? 8000);

        $client = new EurekaClient([
            'eurekaDefaultUrl' => $eurekaUrl,
            'hostName'         => $hostName,
            'appName'          => $appName,
            'ip'               => '127.0.0.1',
            'port'             => [$port, true],
            'homePageUrl'      => "http://localhost:{$port}",
            'statusPageUrl'    => "http://localhost:{$port}/api/support-requests",
            'healthCheckUrl'   => "http://localhost:{$port}/api/support-requests",
        ]);

        try {
            $client->register();
            $output->writeln('✅ Service enregistré avec succès !');
            $output->writeln('Envoi des heartbeats (Ctrl+C pour arrêter)...');

            while (true) {
                sleep(30);
                $client->heartbeat();
                $output->writeln('[' . date('H:i:s') . '] Heartbeat envoyé.');
            }
        } catch (\Exception $e) {
            $output->writeln('❌ Erreur : ' . $e->getMessage());
            return Command::FAILURE;
        }

        return Command::SUCCESS;
    }
}