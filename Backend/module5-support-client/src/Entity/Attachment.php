<?php

namespace App\Entity;

use ApiPlatform\Metadata\ApiResource;
use App\Repository\AttachmentRepository;
use Doctrine\ORM\Mapping as ORM;
use ApiPlatform\Doctrine\Orm\Filter\OrderFilter;
use ApiPlatform\Doctrine\Orm\Filter\SearchFilter;
use ApiPlatform\Metadata\ApiFilter;



#[ORM\Entity(repositoryClass: AttachmentRepository::class)]
#[ORM\HasLifecycleCallbacks]
#[ApiResource]
#[ApiFilter(SearchFilter::class, properties: [
    'supportRequest' => 'exact',
    'nomFichier' => 'partial',
])]
#[ApiFilter(OrderFilter::class, properties: [
    'dateAjout',
    'taille',
    'id',
])]

class Attachment
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    private ?string $nomFichier = null;

    #[ORM\Column(length: 255)]
    private ?string $chemin = null;

    #[ORM\Column]
    private ?int $taille = null;

    #[ORM\Column]
    private ?\DateTime $dateAjout = null;

    #[ORM\ManyToOne(inversedBy: 'attachments')]
    private ?SupportRequest $supportRequest = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNomFichier(): ?string
    {
        return $this->nomFichier;
    }

    public function setNomFichier(string $nomFichier): static
    {
        $this->nomFichier = $nomFichier;

        return $this;
    }

    public function getChemin(): ?string
    {
        return $this->chemin;
    }

    public function setChemin(string $chemin): static
    {
        $this->chemin = $chemin;

        return $this;
    }

    public function getTaille(): ?int
    {
        return $this->taille;
    }

    public function setTaille(int $taille): static
    {
        $this->taille = $taille;

        return $this;
    }

    public function getDateAjout(): ?\DateTime
    {
        return $this->dateAjout;
    }

    public function setDateAjout(\DateTime $dateAjout): static
    {
        $this->dateAjout = $dateAjout;

        return $this;
    }

    public function getSupportRequest(): ?SupportRequest
    {
        return $this->supportRequest;
    }

    public function setSupportRequest(?SupportRequest $supportRequest): static
    {
        $this->supportRequest = $supportRequest;

        return $this;
    }

    #[ORM\PrePersist]
    public function setDateAjoutOnCreate(): void
    {
        $this->dateAjout = new \DateTime();
    }
}
