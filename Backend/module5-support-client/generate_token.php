<?php
require 'vendor/autoload.php';

$kernel = new \App\Kernel('dev', true);
$kernel->boot();
$container = $kernel->getContainer();

$jwtManager = $container->get('lexik_jwt_authentication.jwt_manager');
$user = new \Symfony\Component\Security\Core\User\InMemoryUser('admin', null, ['ROLE_ADMIN']);

echo "TOKEN: " . $jwtManager->create($user) . PHP_EOL;