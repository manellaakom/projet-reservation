#!/bin/bash
set -e

# Run migrations and Eureka registration in background
(
  echo "Waiting for services to be ready..."
  sleep 6
  php bin/console doctrine:migrations:migrate --no-interaction || true
  php bin/console app:eureka:register || true
) &

exec apache2-foreground
