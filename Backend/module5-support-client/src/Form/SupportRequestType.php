<?php

namespace App\Form;

use App\Entity\Category;
use App\Entity\SupportRequest;
use App\Enum\SupportStatus;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\EnumType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class SupportRequestType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('sujet')
            ->add('description')
            ->add('categorie')
            ->add('status', EnumType::class, [
                'class' => SupportStatus::class,
                'choice_label' => fn (SupportStatus $status) => $status->label(),
            ])
            ->add('category', EntityType::class, [
                'class' => Category::class,
                'choice_label' => 'nom',
                'placeholder' => 'Choisir une catégorie',
                'required' => false,
            ])
            ->add('clientId')
            ->add('agentId')
            ->add('reservationId')
            ->add('category', EntityType::class, [
                'class' => Category::class,
                'choice_label' => 'nom',
                'placeholder' => 'Choisir une catégorie',
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
