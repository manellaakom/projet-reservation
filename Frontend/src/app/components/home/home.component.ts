import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface FeatureHighlight {
  icon: string;
  title: string;
  description: string;
  tag: string;
  tagClass: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  readonly features: FeatureHighlight[] = [
    {
      icon: 'pi pi-compass',
      title: 'Recherche Multi-critères Avancée',
      description: 'Filtrage dynamique par ville, dates, prix max, type de transport (vol, train, bus, bateau), options bagages et annulation gratuite.',
      tag: 'Module 2 — Recherche',
      tagClass: 'tag-primary'
    },
    {
      icon: 'pi pi-sliders-h',
      title: 'Comparateur d’Offres Intelligent',
      description: 'Comparez côte-à-côte de 2 à 4 voyages avec calcul automatique de l’écart de prix, de durée et synthèse de recommandation.',
      tag: 'Module 2 — Comparateur',
      tagClass: 'tag-success'
    },
    {
      icon: 'pi pi-sparkles',
      title: 'Suggestions IA & Tendances',
      description: 'Découvrez instantanément les tendances du moment, les meilleures réductions promotionnelles et les offres petits budgets.',
      tag: 'Module 2 — Suggestions',
      tagClass: 'tag-info'
    },
    {
      icon: 'pi pi-shield',
      title: 'Sécurité JWT & Rôles',
      description: 'Contrôle d’accès strict : consultation et recherche publiques, création/modification authentifiées et suppression réservée à ROLE_ADMIN.',
      tag: 'Sécurité Spring',
      tagClass: 'tag-warning'
    },
    {
      icon: 'pi pi-briefcase',
      title: 'Gestion des Bagages & Tarification',
      description: 'Enregistrement des bagages, pesée dynamique, calcul automatique (30, 50, 70 DT) et persistance Redis haute performance.',
      tag: 'Module 1 — Bagages',
      tagClass: 'tag-primary'
    },
    {
      icon: 'pi pi-server',
      title: 'Architecture Microservices & Docker',
      description: 'Communication synchrone OpenFeign, broker asynchrone RabbitMQ, conteneurisation Docker (Alpine Temurin) et découverte Eureka.',
      tag: 'Microservices AWD',
      tagClass: 'tag-neutral'
    }
  ];

  readonly systemStats = [
    { label: 'Catalogue Voyages', value: 'Module 2', icon: 'pi pi-compass' },
    { label: 'Gestion Bagages', value: 'Module 1', icon: 'pi pi-briefcase' },
    { label: 'Sécurité', value: 'JWT & RBAC', icon: 'pi pi-shield' },
    { label: 'Docker & API', value: ':8082 / :9082', icon: 'pi pi-box' }
  ];
}
