<?php

namespace App\Service;

use App\Entity\SupportRequest;
use App\Repository\SupportRequestRepository;
use Doctrine\ORM\EntityManagerInterface;

class SupportRequestService
{
    public function __construct(
        private SupportRequestRepository $repository,
        private EntityManagerInterface $entityManager,
    ) {
    }

    /**
     * @return SupportRequest[]
     */
    public function findAll(): array
    {
        return $this->repository->findBy([], ['dateCreation' => 'DESC']);
    }

    public function find(int $id): ?SupportRequest
    {
        return $this->repository->find($id);
    }

    public function create(SupportRequest $supportRequest): SupportRequest
    {
        $this->entityManager->persist($supportRequest);
        $this->entityManager->flush();

        return $supportRequest;
    }

    public function update(SupportRequest $supportRequest): SupportRequest
    {
        $this->entityManager->flush();

        return $supportRequest;
    }

    public function delete(SupportRequest $supportRequest): void
    {
        $this->entityManager->remove($supportRequest);
        $this->entityManager->flush();
    }

    /**
     * @return SupportRequest[]
     */
    public function findByStatus(string $status): array
    {
        return $this->repository->findBy(['status' => $status]);
    }
}