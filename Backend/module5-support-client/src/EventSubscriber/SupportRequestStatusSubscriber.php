<?php

namespace App\EventSubscriber;

use App\Entity\SupportRequest;
use App\Enum\SupportStatus;
use App\Service\NotificationService;
use Doctrine\Bundle\DoctrineBundle\Attribute\AsDoctrineListener;
use Doctrine\ORM\Event\PostUpdateEventArgs;
use Doctrine\ORM\Events;

#[AsDoctrineListener(event: Events::postUpdate)]
class SupportRequestStatusSubscriber
{
    public function __construct(
        private NotificationService $notificationService,
    ) {
    }

    public function postUpdate(PostUpdateEventArgs $args): void
    {
        $entity = $args->getObject();

        if (!$entity instanceof SupportRequest) {
            return;
        }

        $changeSet = $args->getObjectManager()->getUnitOfWork()->getEntityChangeSet($entity);

        if (!isset($changeSet['status'])) {
            return;
        }

        [$oldStatus, $newStatus] = $changeSet['status'];

        if ($oldStatus === $newStatus) {
            return;
        }

        $oldLabel = $this->getStatusLabel($oldStatus);
        $this->notificationService->sendStatusChangeEmail($entity, $oldLabel);
    }

    private function getStatusLabel(mixed $status): string
    {
        if ($status instanceof SupportStatus) {
            return $status->label();
        }

        if (is_string($status)) {
            $enum = SupportStatus::tryFrom($status);
            return $enum?->label() ?? ucfirst($status);
        }

        return 'Inconnu';
    }
}
