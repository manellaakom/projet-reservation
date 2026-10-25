<?php

namespace App\Controller;

use App\Entity\SupportResponse;
use App\Form\SupportResponseType;
use App\Repository\SupportResponseRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/support/response')]
final class SupportResponseController extends AbstractController
{
    #[Route(name: 'app_support_response_index', methods: ['GET'])]
    public function index(SupportResponseRepository $supportResponseRepository): Response
    {
        return $this->render('support_response/index.html.twig', [
            'support_responses' => $supportResponseRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_support_response_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $supportResponse = new SupportResponse();
        $form = $this->createForm(SupportResponseType::class, $supportResponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($supportResponse);
            $entityManager->flush();

            return $this->redirectToRoute('app_support_response_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('support_response/new.html.twig', [
            'support_response' => $supportResponse,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_support_response_show', methods: ['GET'])]
    public function show(SupportResponse $supportResponse): Response
    {
        return $this->render('support_response/show.html.twig', [
            'support_response' => $supportResponse,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_support_response_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, SupportResponse $supportResponse, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(SupportResponseType::class, $supportResponse);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_support_response_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('support_response/edit.html.twig', [
            'support_response' => $supportResponse,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_support_response_delete', methods: ['POST'])]
    public function delete(Request $request, SupportResponse $supportResponse, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$supportResponse->getId(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($supportResponse);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_support_response_index', [], Response::HTTP_SEE_OTHER);
    }
}
