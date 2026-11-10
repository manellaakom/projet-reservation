<?php

namespace App\Controller;

use App\Entity\Attachment;
use App\Entity\SupportRequest;
use Doctrine\ORM\EntityManagerInterface;
use OpenApi\Attributes as OA;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\String\Slugger\SluggerInterface;

#[Route('/api/attachments')]
class AttachmentUploadController extends AbstractController
{
    public function __construct(
        private EntityManagerInterface $em,
        private SluggerInterface $slugger,
        private string $uploadDir,
    ) {
    }

    #[Route('/upload', name: 'api_attachment_upload', methods: ['POST'])]
    #[OA\Post(
        path: '/api/attachments/upload',
        summary: 'Upload a file and attach it to a SupportRequest',
        description: 'Upload un fichier (PDF, JPG, PNG, GIF, max 10 Mo) et l\'associe à une demande.',
        tags: ['Attachment'],
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\MediaType(
                mediaType: 'multipart/form-data',
                schema: new OA\Schema(
                    type: 'object',
                    required: ['file', 'supportRequestId'],
                    properties: [
                        new OA\Property(
                            property: 'file',
                            type: 'string',
                            format: 'binary',
                            description: 'Le fichier à uploader (max 10 Mo)'
                        ),
                        new OA\Property(
                            property: 'supportRequestId',
                            type: 'integer',
                            example: 5,
                            description: 'L\'ID de la SupportRequest associée'
                        ),
                    ]
                )
            )
        ),
        responses: [
            new OA\Response(
                response: 201,
                description: 'Fichier uploadé avec succès',
                content: new OA\JsonContent(
                    type: 'object',
                    properties: [
                        new OA\Property(property: 'id', type: 'integer', example: 2),
                        new OA\Property(property: 'nomFichier', type: 'string', example: 'capture.png'),
                        new OA\Property(property: 'chemin', type: 'string', example: '/uploads/attachments/capture-abc123.png'),
                        new OA\Property(property: 'taille', type: 'integer', example: 245000),
                        new OA\Property(property: 'dateAjout', type: 'string', format: 'date-time'),
                        new OA\Property(property: 'supportRequestId', type: 'integer', example: 5),
                    ]
                )
            ),
            new OA\Response(response: 400, description: 'Erreur de validation (taille, type, fichier manquant)'),
            new OA\Response(response: 404, description: 'SupportRequest introuvable'),
            new OA\Response(response: 500, description: 'Erreur serveur'),
        ]
    )]
    public function upload(Request $request): JsonResponse
    {
        $file = $request->files->get('file');
        if (!$file) {
            return new JsonResponse(['error' => 'Aucun fichier fourni'], Response::HTTP_BAD_REQUEST);
        }

        $supportRequestId = $request->request->get('supportRequestId');
        if (!$supportRequestId) {
            return new JsonResponse(['error' => 'supportRequestId manquant'], Response::HTTP_BAD_REQUEST);
        }

        $supportRequest = $this->em->getRepository(SupportRequest::class)->find($supportRequestId);
        if (!$supportRequest) {
            return new JsonResponse(['error' => 'SupportRequest introuvable'], Response::HTTP_NOT_FOUND);
        }

        $originalName = $file->getClientOriginalName();
        $fileSize = $file->getSize();
        $mimeType = $file->getMimeType();

        $maxSize = 10 * 1024 * 1024;
        if ($fileSize > $maxSize) {
            return new JsonResponse(['error' => 'Fichier trop volumineux (max 10 Mo)'], Response::HTTP_BAD_REQUEST);
        }

        $allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/gif'];
        if (!in_array($mimeType, $allowedTypes, true)) {
            return new JsonResponse([
                'error' => 'Type non autorisé. Types acceptés : PDF, JPG, PNG, GIF'
            ], Response::HTTP_BAD_REQUEST);
        }

        $originalFilename = pathinfo($originalName, PATHINFO_FILENAME);
        $safeFilename = $this->slugger->slug($originalFilename);
        $newFilename = $safeFilename . '-' . uniqid() . '.' . $file->guessExtension();

        try {
            $file->move($this->uploadDir, $newFilename);
        } catch (FileException $e) {
            return new JsonResponse(['error' => 'Erreur d\'enregistrement du fichier'], Response::HTTP_INTERNAL_SERVER_ERROR);
        }

        $attachment = new Attachment();
        $attachment->setNomFichier($originalName);
        $attachment->setChemin('/uploads/attachments/' . $newFilename);
        $attachment->setTaille($fileSize);
        $attachment->setSupportRequest($supportRequest);

        $this->em->persist($attachment);
        $this->em->flush();

        return new JsonResponse([
            'id' => $attachment->getId(),
            'nomFichier' => $attachment->getNomFichier(),
            'chemin' => $attachment->getChemin(),
            'taille' => $attachment->getTaille(),
            'dateAjout' => $attachment->getDateAjout()?->format('c'),
            'supportRequestId' => $supportRequest->getId(),
        ], Response::HTTP_CREATED);
    }
}