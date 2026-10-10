<?php

namespace App\Entity;

use App\Repository\CategoryRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use ApiPlatform\Doctrine\Orm\Filter\SearchFilter;
use ApiPlatform\Metadata\ApiFilter;

#[ApiResource]
#[ApiFilter(SearchFilter::class, properties: [
    'nom' => 'partial',
    'description' => 'partial',
])]

#[ORM\Entity(repositoryClass: CategoryRepository::class)]
class Category
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 100)]
    private ?string $nom = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $description = null;

    /**
     * @var Collection<int, SupportRequest>
     */
    #[ORM\OneToMany(targetEntity: SupportRequest::class, mappedBy: 'category')]
    private Collection $supportRequests;

    public function __construct()
    {
        $this->supportRequests = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(?string $description): static
    {
        $this->description = $description;

        return $this;
    }

    /**
     * @return Collection<int, SupportRequest>
     */
    public function getSupportRequests(): Collection
    {
        return $this->supportRequests;
    }

    public function addSupportRequest(SupportRequest $supportRequest): static
    {
        if (!$this->supportRequests->contains($supportRequest)) {
            $this->supportRequests->add($supportRequest);
            $supportRequest->setCategory($this);
        }

        return $this;
    }

    public function removeSupportRequest(SupportRequest $supportRequest): static
    {
        if ($this->supportRequests->removeElement($supportRequest)) {
            // set the owning side to null (unless already changed)
            if ($supportRequest->getCategory() === $this) {
                $supportRequest->setCategory(null);
            }
        }

        return $this;
    }
}
