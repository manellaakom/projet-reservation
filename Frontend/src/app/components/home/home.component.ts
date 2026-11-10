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
      title: 'Recherche Multi-critères',
      description: 'Filtrage dynamique par destination, dates, budget, transport (vol, train, bateau) et services à bord.',
      tag: 'Voyages & Vols',
      tagClass: 'tag-primary'
    },
    {
      icon: 'pi pi-sliders-h',
      title: 'Comparateur d’Offres',
      description: 'Comparez côte-à-côte jusqu’à 4 offres avec calcul automatique des écarts de prix et des avantages.',
      tag: 'Comparateur',
      tagClass: 'tag-success'
    },
    {
      icon: 'pi pi-sparkles',
      title: 'Suggestions Intelligentes',
      description: 'Découvrez instantanément les tendances du moment, les promotions exclusives et les petits budgets.',
      tag: 'Promotions',
      tagClass: 'tag-info'
    },
    {
      icon: 'pi pi-briefcase',
      title: 'Gestion des Bagages',
      description: 'Enregistrement rapide des bagages, pesée dynamique, calcul automatique du tarif et rattachement aux dossiers passagers.',
      tag: 'Bagages',
      tagClass: 'tag-primary'
    },
    {
      icon: 'pi pi-shield',
      title: 'Plafond de Sécurité',
      description: 'Contrôle automatique du plafond de 30 kg par unité et vérification de conformité en temps réel.',
      tag: 'Sécurité Bagages',
      tagClass: 'tag-warning'
    },
    {
      icon: 'pi pi-check-circle',
      title: 'Réservation en Ligne',
      description: 'Vérification instantanée de la disponibilité des places et confirmation immédiate de votre itinéraire.',
      tag: 'Réservation',
      tagClass: 'tag-neutral'
    }
  ];

  readonly systemStats = [
    { label: 'Catalogue', value: 'Vols & Séjours', icon: 'pi pi-compass' },
    { label: 'Service Bagages', value: 'Enregistrement', icon: 'pi pi-briefcase' },
    { label: 'Destinations', value: 'Europe & Maghreb', icon: 'pi pi-map-marker' },
    { label: 'Plateforme', value: 'En ligne', icon: 'pi pi-check' }
  ];
}
