<?php

namespace App\Controller;

use App\Entity\SupportRequest;
use App\Form\SupportRequestType;
use App\Repository\SupportRequestRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/support/request')]
final class SupportRequestController extends AbstractController
{
    #[Route(name: 'app_support_request_index', methods: ['GET'])]
    public function index(SupportRequestRepository $supportRequestRepository): Response
    {
        return $this->render('support_request/index.html.twig', [
            'support_requests' => $supportRequestRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_support_request_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $supportRequest = new SupportRequest();
        $form = $this->createForm(SupportRequestType::class, $supportRequest);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($supportRequest);
            $entityManager->flush();

            return $this->redirectToRoute('app_support_request_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('support_request/new.html.twig', [
            'support_request' => $supportRequest,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_support_request_show', methods: ['GET'])]
    public function show(SupportRequest $supportRequest): Response
    {
        return $this->render('support_request/show.html.twig', [
            'support_request' => $supportRequest,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_support_request_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, SupportRequest $supportRequest, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(SupportRequestType::class, $supportRequest);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_support_request_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('support_request/edit.html.twig', [
            'support_request' => $supportRequest,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_support_request_delete', methods: ['POST'])]
    public function delete(Request $request, SupportRequest $supportRequest, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$supportRequest->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($supportRequest);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_support_request_index', [], Response::HTTP_SEE_OTHER);
    }
}
