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
      icon: 'pi pi-box',
      title: 'Gestion Centralisée des Bagages',
      description: 'Enregistrez, modifiez et supprimez vos bagages en toute simplicité avec persistance haute performance dans Redis.',
      tag: 'CRUD Temps Réel',
      tagClass: 'tag-primary'
    },
    {
      icon: 'pi pi-wallet',
      title: 'Tarification Automatique',
      description: 'Calcul instantané selon le poids pesé : 30 DT (0–10 kg), 50 DT (10–20 kg) et 70 DT (20–30 kg) appliqué par le Backend.',
      tag: 'Règles Métier',
      tagClass: 'tag-success'
    },
    {
      icon: 'pi pi-shield',
      title: 'Contrôle & Sécurité de Charge',
      description: 'Validation stricte du plafond de 30.0 kg et vérification de conformité entre le type de bagage et la charge admissible.',
      tag: 'Plafond 30 kg',
      tagClass: 'tag-warning'
    },
    {
      icon: 'pi pi-link',
      title: 'Association aux Réservations',
      description: 'Rattachez instantanément un bagage à un dossier voyageur via l\'endpoint dédié PATCH de l\'API Gateway.',
      tag: 'Synchronisation',
      tagClass: 'tag-info'
    },
    {
      icon: 'pi pi-chart-pie',
      title: 'Statistiques & Supervision',
      description: 'Indicateurs en direct sur le poids total cumulé, le chiffre d\'affaires bagages et le poids moyen par unité.',
      tag: 'Analytique Live',
      tagClass: 'tag-primary'
    },
    {
      icon: 'pi pi-file-excel',
      title: 'Export & Traçabilité',
      description: 'Exportez vos listes de bagages filtrées vers Excel (format CSV UTF-8) et suivez l\'historique des flux opérationnels.',
      tag: 'Export Excel',
      tagClass: 'tag-neutral'
    }
  ];

  readonly systemStats = [
    { label: 'Plafond de sécurité', value: '30.0 kg', icon: 'pi pi-lock' },
    { label: 'Paliers tarifaires', value: '3 Niveaux', icon: 'pi pi-tags' },
    { label: 'Persistance', value: 'Redis Hash', icon: 'pi pi-database' },
    { label: 'API Gateway', value: 'Port 8080', icon: 'pi pi-server' }
  ];
}
