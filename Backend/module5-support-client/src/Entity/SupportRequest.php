<?php

namespace App\Entity;

use App\Enum\SupportStatus;
use App\Repository\SupportRequestRepository;
use Doctrine\ORM\Mapping as ORM;
use ApiPlatform\Metadata\ApiResource;
use ApiPlatform\Doctrine\Orm\Filter\OrderFilter;
use ApiPlatform\Doctrine\Orm\Filter\SearchFilter;
use ApiPlatform\Metadata\ApiFilter;

#[ORM\Entity(repositoryClass: SupportRequestRepository::class)]
#[ORM\HasLifecycleCallbacks]
#[ApiResource]
#[ApiFilter(SearchFilter::class, properties: [
    'status' => 'exact',
    'clientId' => 'exact',
    'agentId' => 'exact',
    'reservationId' => 'exact',
    'sujet' => 'partial',
    'description' => 'partial',
    'categorie' => 'partial',
])]
#[ApiFilter(OrderFilter::class, properties: [
    'dateCreation',
    'dateMiseAJour',
    'id',
    'status',
])]
class SupportRequest
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    private ?string $sujet = null;

    #[ORM\Column(length: 255)]
    private ?string $description = null;

    #[ORM\Column(length: 100)]
    private ?string $categorie = null;

    #[ORM\Column(length: 50, enumType: SupportStatus::class)]
    private ?SupportStatus $status = null;

    #[ORM\Column]
    private ?\DateTime $dateCreation = null;

    #[ORM\Column(nullable: true)]
    private ?\DateTime $dateMiseAJour = null;

    #[ORM\Column(nullable: true)]
    private ?int $clientId = null;

    #[ORM\Column(nullable: true)]
    private ?int $agentId = null;

    #[ORM\Column(nullable: true)]
    private ?int $reservationId = null;

    public function getId(): ?int { return $this->id; }

    public function getSujet(): ?string { return $this->sujet; }
    public function setSujet(string $sujet): static { $this->sujet = $sujet; return $this; }

    public function getDescription(): ?string { return $this->description; }
    public function setDescription(string $description): static { $this->description = $description; return $this; }

    public function getCategorie(): ?string { return $this->categorie; }
    public function setCategorie(string $categorie): static { $this->categorie = $categorie; return $this; }

    public function getStatus(): ?SupportStatus { return $this->status; }
    public function setStatus(?SupportStatus $status): static { $this->status = $status; return $this; }

    public function getDateCreation(): ?\DateTime { return $this->dateCreation; }
    public function setDateCreation(\DateTime $dateCreation): static { $this->dateCreation = $dateCreation; return $this; }

    public function getDateMiseAJour(): ?\DateTime { return $this->dateMiseAJour; }
    public function setDateMiseAJour(?\DateTime $dateMiseAJour): static { $this->dateMiseAJour = $dateMiseAJour; return $this; }

    public function getClientId(): ?int { return $this->clientId; }
    public function setClientId(?int $clientId): static { $this->clientId = $clientId; return $this; }

    public function getAgentId(): ?int { return $this->agentId; }
    public function setAgentId(?int $agentId): static { $this->agentId = $agentId; return $this; }

    public function getReservationId(): ?int { return $this->reservationId; }
    public function setReservationId(?int $reservationId): static { $this->reservationId = $reservationId; return $this; }

    #[ORM\PrePersist]
    public function setDatesOnCreate(): void
    {
        $this->dateCreation = new \DateTime();
        $this->dateMiseAJour = new \DateTime();

        if ($this->status === null) {
            $this->status = SupportStatus::EN_COURS;
        }
    }

    #[ORM\PreUpdate]
    public function setDateOnUpdate(): void
    {
        $this->dateMiseAJour = new \DateTime();
    }
}
