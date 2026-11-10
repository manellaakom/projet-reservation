<?php

namespace App\Controller;

use App\Entity\SupportRequest;
use App\Enum\SupportStatus;
use App\Repository\SupportRequestRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/api/support-requests')]
class SupportRequestApiController extends AbstractController
{
    public function __construct(
        private EntityManagerInterface $entityManager,
        private SupportRequestRepository $repository
    ) {
    }

    #[Route('', name: 'api_support_requests_list', methods: ['GET'])]
    public function list(Request $request): JsonResponse
    {
        $status = $request->query->get('status');
        $search = $request->query->get('search');
        $reservationId = $request->query->get('reservationId');
        $clientId = $request->query->get('clientId');

        $qb = $this->repository->createQueryBuilder('s');

        if ($status && $status !== 'ALL') {
            try {
                $statusEnum = SupportStatus::from($status);
                $qb->andWhere('s.status = :status')
                   ->setParameter('status', $statusEnum);
            } catch (\ValueError $e) {
                // Ignore invalid status enum or match string
            }
        }

        if ($reservationId) {
            $qb->andWhere('s.reservationId = :reservationId')
               ->setParameter('reservationId', (int)$reservationId);
        }

        if ($clientId) {
            $qb->andWhere('s.clientId = :clientId')
               ->setParameter('clientId', (int)$clientId);
        }

        if ($search) {
            $qb->andWhere('LOWER(s.sujet) LIKE :search OR LOWER(s.description) LIKE :search OR LOWER(s.categorie) LIKE :search')
               ->setParameter('search', '%' . strtolower($search) . '%');
        }

        $qb->orderBy('s.dateCreation', 'DESC');
        $requests = $qb->getQuery()->getResult();

        $data = array_map(fn(SupportRequest $s) => $this->serializeRequest($s), $requests);

        return new JsonResponse($data, Response::HTTP_OK);
    }

    #[Route('/stats', name: 'api_support_requests_stats', methods: ['GET'])]
    public function stats(): JsonResponse
    {
        $all = $this->repository->findAll();
        $total = count($all);
        $enAttente = 0;
        $enCours = 0;
        $resolu = 0;
        $ferme = 0;

        foreach ($all as $req) {
            $st = $req->getStatus()?->value ?? 'EN_ATTENTE';
            match ($st) {
                'EN_ATTENTE' => $enAttente++,
                'EN_COURS' => $enCours++,
                'RESOLU' => $resolu++,
                'FERME' => $ferme++,
                default => null,
            };
        }

        return new JsonResponse([
            'total' => $total,
            'enAttente' => $enAttente,
            'enCours' => $enCours,
            'resolu' => $resolu,
            'ferme' => $ferme,
        ], Response::HTTP_OK);
    }

    #[Route('/{id}', name: 'api_support_requests_show', methods: ['GET'], requirements: ['id' => '\d+'])]
    public function show(int $id): JsonResponse
    {
        $supportRequest = $this->repository->find($id);
        if (!$supportRequest) {
            return new JsonResponse(['error' => 'Demande de support introuvable.'], Response::HTTP_NOT_FOUND);
        }

        return new JsonResponse($this->serializeRequest($supportRequest), Response::HTTP_OK);
    }

    #[Route('', name: 'api_support_requests_create', methods: ['POST'])]
    public function create(Request $request): JsonResponse
    {
        $payload = json_decode($request->getContent(), true);
        if (!$payload) {
            return new JsonResponse(['error' => 'Format JSON invalide.'], Response::HTTP_BAD_REQUEST);
        }

        if (empty($payload['sujet']) || empty($payload['description'])) {
            return new JsonResponse(['error' => 'Le sujet et la description sont obligatoires.'], Response::HTTP_UNPROCESSABLE_ENTITY);
        }

        $supportRequest = new SupportRequest();
        $supportRequest->setSujet($payload['sujet']);
        $supportRequest->setDescription($payload['description']);
        $supportRequest->setCategorie($payload['categorie'] ?? 'Support Général');

        if (!empty($payload['status'])) {
            try {
                $supportRequest->setStatus(SupportStatus::from($payload['status']));
            } catch (\ValueError $e) {
                $supportRequest->setStatus(SupportStatus::EN_ATTENTE);
            }
        } else {
            $supportRequest->setStatus(SupportStatus::EN_ATTENTE);
        }

        if (isset($payload['clientId']) && $payload['clientId'] !== null && $payload['clientId'] !== '') {
            $supportRequest->setClientId((int)$payload['clientId']);
        }

        if (isset($payload['agentId']) && $payload['agentId'] !== null && $payload['agentId'] !== '') {
            $supportRequest->setAgentId((int)$payload['agentId']);
        }

        if (isset($payload['reservationId']) && $payload['reservationId'] !== null && $payload['reservationId'] !== '') {
            $supportRequest->setReservationId((int)$payload['reservationId']);
        }

        $this->entityManager->persist($supportRequest);
        $this->entityManager->flush();

        return new JsonResponse($this->serializeRequest($supportRequest), Response::HTTP_CREATED);
    }

    #[Route('/{id}', name: 'api_support_requests_update', methods: ['PUT', 'PATCH'], requirements: ['id' => '\d+'])]
    public function update(int $id, Request $request): JsonResponse
    {
        $supportRequest = $this->repository->find($id);
        if (!$supportRequest) {
            return new JsonResponse(['error' => 'Demande de support introuvable.'], Response::HTTP_NOT_FOUND);
        }

        $payload = json_decode($request->getContent(), true);
        if (!$payload) {
            return new JsonResponse(['error' => 'Format JSON invalide.'], Response::HTTP_BAD_REQUEST);
        }

        if (isset($payload['sujet']) && trim($payload['sujet']) !== '') {
            $supportRequest->setSujet($payload['sujet']);
        }

        if (isset($payload['description']) && trim($payload['description']) !== '') {
            $supportRequest->setDescription($payload['description']);
        }

        if (isset($payload['categorie']) && trim($payload['categorie']) !== '') {
            $supportRequest->setCategorie($payload['categorie']);
        }

        if (!empty($payload['status'])) {
            try {
                $supportRequest->setStatus(SupportStatus::from($payload['status']));
            } catch (\ValueError $e) {
                // keep current
            }
        }

        if (array_key_exists('clientId', $payload)) {
            $supportRequest->setClientId($payload['clientId'] !== null && $payload['clientId'] !== '' ? (int)$payload['clientId'] : null);
        }

        if (array_key_exists('agentId', $payload)) {
            $supportRequest->setAgentId($payload['agentId'] !== null && $payload['agentId'] !== '' ? (int)$payload['agentId'] : null);
        }

        if (array_key_exists('reservationId', $payload)) {
            $supportRequest->setReservationId($payload['reservationId'] !== null && $payload['reservationId'] !== '' ? (int)$payload['reservationId'] : null);
        }

        $supportRequest->setDateMiseAJour(new \DateTime());
        $this->entityManager->flush();

        return new JsonResponse($this->serializeRequest($supportRequest), Response::HTTP_OK);
    }

    #[Route('/{id}', name: 'api_support_requests_delete', methods: ['DELETE'], requirements: ['id' => '\d+'])]
    public function delete(int $id): JsonResponse
    {
        $supportRequest = $this->repository->find($id);
        if (!$supportRequest) {
            return new JsonResponse(['error' => 'Demande de support introuvable.'], Response::HTTP_NOT_FOUND);
        }

        $this->entityManager->remove($supportRequest);
        $this->entityManager->flush();

        return new JsonResponse(['message' => 'Demande de support supprimée avec succès.'], Response::HTTP_OK);
    }

    private function serializeRequest(SupportRequest $s): array
    {
        return [
            'id' => $s->getId(),
            'sujet' => $s->getSujet(),
            'description' => $s->getDescription(),
            'categorie' => $s->getCategorie(),
            'status' => $s->getStatus()?->value ?? 'EN_ATTENTE',
            'dateCreation' => $s->getDateCreation()?->format(\DateTimeInterface::ATOM),
            'dateMiseAJour' => $s->getDateMiseAJour()?->format(\DateTimeInterface::ATOM),
            'clientId' => $s->getClientId(),
            'agentId' => $s->getAgentId(),
            'reservationId' => $s->getReservationId(),
        ];
    }
}
