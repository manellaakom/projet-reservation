<?php

namespace App\Repository;

use App\Entity\SupportRequest;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<SupportRequest>
 */
class SupportRequestRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, SupportRequest::class);
    }
    public function setDateMiseAJour(?\DateTimeImmutable $dateMiseAJour): static
    {
        $this->dateMiseAJour = $dateMiseAJour;
        return $this;
    }

    #[ORM\PrePersist]
    public function setDatesOnCreate(): void
    {
        $this->dateCreation = new \DateTimeImmutable();
        $this->dateMiseAJour = new \DateTimeImmutable();
    }

    #[ORM\PreUpdate]
    public function setDateOnUpdate(): void
    {
        $this->dateMiseAJour = new \DateTimeImmutable();
    }


//    /**
//     * @return SupportRequest[] Returns an array of SupportRequest objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('s')
//            ->andWhere('s.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('s.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?SupportRequest
//    {
//        return $this->createQueryBuilder('s')
//            ->andWhere('s.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
