<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20261009155243 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE support_request ADD category_id INT DEFAULT NULL');
        $this->addSql('ALTER TABLE support_request ADD CONSTRAINT FK_86A2876312469DE2 FOREIGN KEY (category_id) REFERENCES category (id)');
        $this->addSql('CREATE INDEX IDX_86A2876312469DE2 ON support_request (category_id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE support_request DROP CONSTRAINT FK_86A2876312469DE2');
        $this->addSql('DROP INDEX IDX_86A2876312469DE2');
        $this->addSql('ALTER TABLE support_request DROP category_id');
    }
}
