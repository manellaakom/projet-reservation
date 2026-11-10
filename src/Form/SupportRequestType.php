<?php

namespace App\Form;

use App\Entity\SupportRequest;
use App\Enum\SupportStatus;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\EnumType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class SupportRequestType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('sujet', TextType::class, [
                'label' => 'Sujet',
            ])
            ->add('description', TextareaType::class, [
                'label' => 'Description',
            ])
            ->add('categorie', TextType::class, [
                'label' => 'Catégorie',
            ])
            ->add('status', EnumType::class, [
                'class' => SupportStatus::class,
                'label' => 'Statut',
                'required' => false,
            ])
            ->add('clientId', IntegerType::class, [
                'label' => 'ID Client',
                'required' => false,
            ])
            ->add('agentId', IntegerType::class, [
                'label' => 'ID Agent',
                'required' => false,
            ])
            ->add('reservationId', IntegerType::class, [
                'label' => 'ID Réservation',
                'required' => false,
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => SupportRequest::class,
        ]);
    }
}
