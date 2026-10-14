<?php

namespace App\Service;

use App\Entity\SupportRequest;
use App\Enum\SupportStatus;
use Psr\Log\LoggerInterface;
use Symfony\Bridge\Twig\Mime\TemplatedEmail;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Address;

class NotificationService
{
    public function __construct(
        private MailerInterface $mailer,
        private LoggerInterface $logger,
    ) {
    }

    public function sendStatusChangeEmail(SupportRequest $supportRequest, string $oldStatus): void
    {
        $clientEmail = 'client@example.com';

        // Gérer le cas où le status est une string (Doctrine) ou un enum
        $statusLabel = $this->getStatusLabel($supportRequest->getStatus());

        $email = (new TemplatedEmail())
            ->from(new Address('noreply@support-client.local', 'Support Client'))
            ->to($clientEmail)
            ->subject(sprintf(
                'Changement de statut de votre demande #%d : %s',
                $supportRequest->getId(),
                $statusLabel
            ))
            ->htmlTemplate('emails/status_change.html.twig')
            ->context([
                'supportRequest' => $supportRequest,
                'oldStatus' => $oldStatus,
                'newStatusLabel' => $statusLabel,
            ]);

        try {
            $this->mailer->send($email);
            $this->logger->info('Email envoyé pour SupportRequest', [
                'id' => $supportRequest->getId(),
                'oldStatus' => $oldStatus,
                'newStatus' => $statusLabel,
            ]);
        } catch (\Exception $e) {
            $this->logger->error('Erreur lors de l\'envoi de l\'email', [
                'error' => $e->getMessage(),
            ]);
        }
    }

    private function getStatusLabel(mixed $status): string
    {
        // Si c'est déjà un enum
        if ($status instanceof SupportStatus) {
            return $status->label();
        }

        // Si c'est une string (valeur brute en base)
        if (is_string($status)) {
            $enum = SupportStatus::tryFrom($status);
            return $enum?->label() ?? ucfirst($status);
        }

        return 'Inconnu';
    }
}
