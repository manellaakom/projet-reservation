<?php

namespace App\Entity;

use App\Repository\SupportResponseRepository;
use Doctrine\ORM\Mapping as ORM;
use ApiPlatform\Metadata\ApiResource;
use ApiPlatform\Doctrine\Orm\Filter\OrderFilter;
use ApiPlatform\Doctrine\Orm\Filter\SearchFilter;
use ApiPlatform\Metadata\ApiFilter;


#[ORM\Entity(repositoryClass: SupportResponseRepository::class)]
#[ApiResource]
#[ApiFilter(SearchFilter::class, properties: [
    'supportRequest' => 'exact',
    'auteur' => 'partial',
    'contenu' => 'partial',
])]
#[ApiFilter(OrderFilter::class, properties: [
    'dateReponse',
    'id',
])]

class SupportResponse
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(type: 'text')]
    private ?string $contenu = null;

    #[ORM\Column(length: 100)]
    private ?string $auteur = null;

    #[ORM\Column]
    private ?\DateTime $dateReponse = null;

    #[ORM\ManyToOne(inversedBy: 'supportResponses')]
    #[ORM\JoinColumn(nullable: false)]
    private ?SupportRequest $supportRequest = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getContenu(): ?string
    {
        return $this->contenu;
    }

    public function setContenu(string $contenu): static
    {
        $this->contenu = $contenu;

        return $this;
    }

    public function getAuteur(): ?string
    {
        return $this->auteur;
    }

    public function setAuteur(string $auteur): static
    {
        $this->auteur = $auteur;

        return $this;
    }

    public function getDateReponse(): ?\DateTime
    {
        return $this->dateReponse;
    }

    public function setDateReponse(\DateTime $dateReponse): static
    {
        $this->dateReponse = $dateReponse;

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
}
