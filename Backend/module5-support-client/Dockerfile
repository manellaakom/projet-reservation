FROM php:8.4-apache
RUN apt-get update && apt-get install -y libpq-dev && docker-php-ext-install pdo pdo_pgsql
RUN a2enmod rewrite
WORKDIR /var/www/html
COPY . .
RUN chown -R www-data:www-data /var/www/html/var /var/www/html/public
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer
RUN composer install --no-interaction --optimize-autoloader
EXPOSE 80
