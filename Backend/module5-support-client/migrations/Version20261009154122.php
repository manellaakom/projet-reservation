<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20261009154122 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE support_request ADD client_id INT DEFAULT NULL');
        $this->addSql('ALTER TABLE support_request ADD agent_id INT DEFAULT NULL');
        $this->addSql('ALTER TABLE support_request ADD reservation_id INT DEFAULT NULL');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE support_request DROP client_id');
        $this->addSql('ALTER TABLE support_request DROP agent_id');
        $this->addSql('ALTER TABLE support_request DROP reservation_id');
    }
}
