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

        $client = new EurekaClient([
            'eurekaDefaultUrl' => 'http://localhost:8761/eureka',
            'hostName'         => 'localhost',
            'appName'          => 'SUPPORT-CLIENT-BACKEND',
            'ip'               => '127.0.0.1',
            'port'             => ['8000', true],
            'homePageUrl'      => 'http://localhost:8000',
            'statusPageUrl'    => 'http://localhost:8000/api',
            'healthCheckUrl'   => 'http://localhost:8000/api',
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