<?php
require 'vendor/autoload.php';

use phpseclib4\Crypt\RSA;

// Génère une clé privée RSA de 2048 bits
$private = RSA::createKey(2048);

// Sauvegarde la clé privée au format PEM (PKCS8)
file_put_contents('config/jwt/private.pem', $private->toString('PKCS8'));

// Sauvegarde la clé publique au format PEM
file_put_contents('config/jwt/public.pem', $private->getPublicKey()->toString('PKCS8'));

echo "Clés générées avec succès (phpseclib v4) !" . PHP_EOL;