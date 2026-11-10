FROM php:8.4-apache
RUN apt-get update && apt-get install -y libpq-dev git unzip && docker-php-ext-install pdo pdo_pgsql
RUN a2enmod rewrite

COPY apache.conf /etc/apache2/sites-available/000-default.conf

WORKDIR /var/www/html
COPY . .
RUN mkdir -p /var/www/html/var /var/www/html/public && chown -R www-data:www-data /var/www/html/var /var/www/html/public
ENV COMPOSER_ALLOW_SUPERUSER=1
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer
RUN composer install --no-interaction --optimize-autoloader
EXPOSE 80
