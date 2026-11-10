import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RechercheConsultationService } from '../../services/recherche-consultation.service';
import { BagageService } from '../../services/bagage.service';
import { AuthService, UserProfile } from '../../services/auth.service';
import { StatistiquesCatalogue, VoyageOffre } from '../../models/recherche.model';
import { BagageStatistics } from '../../models/bagage.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  statsCatalogue: StatistiquesCatalogue | null = null;
  statsBagages: BagageStatistics | null = null;
  featuredVoyages: VoyageOffre[] = [];
  currentUser: UserProfile | null = null;
  loading: boolean = true;
  error: string | null = null;

  // Données de secours fiables si les microservices mettent du temps à répondre
  defaultFeaturedVoyages: VoyageOffre[] = [
    {
      id: 18,
      codeVoyage: 'VOY-TUN-DJE-01',
      titre: 'Vol Tunis Carthage → Djerba Zarzis',
      description: 'Vol direct intérieur quotidien avec service à bord et bagage inclus.',
      villeDepart: 'Tunis',
      villeDestination: 'Djerba',
      paysDestination: 'Tunisie',
      compagnie: 'Tunisair Express',
      typeTransport: 'VOL',
      categorie: 'ECONOMIQUE',
      dateDepart: '2026-11-20T08:30:00',
      dateArrivee: '2026-11-20T09:25:00',
      dureeMinutes: 55,
      dureeFormatee: '55m',
      escales: 0,
      prixBase: 85.0,
      prixPromo: 65.0,
      prixEffectif: 65.0,
      pourcentageReduction: 24,
      enPromotion: true,
      placesDisponibles: 18,
      noteMoyenne: 4.8,
      nombreAvis: 34,
      nombreConsultations: 142,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      statut: 'DISPONIBLE'
    },
    {
      id: 19,
      codeVoyage: 'VOY-TUN-SOUS-03',
      titre: 'Train Express Climatisé Tunis → Sousse',
      description: 'Liaison ferroviaire grand confort avec sièges inclinables et prises électriques.',
      villeDepart: 'Tunis',
      villeDestination: 'Sousse',
      paysDestination: 'Tunisie',
      compagnie: 'SNCFT',
      typeTransport: 'TRAIN',
      categorie: 'CONFORT',
      dateDepart: '2026-11-21T07:15:00',
      dateArrivee: '2026-11-21T09:15:00',
      dureeMinutes: 120,
      dureeFormatee: '2h 00m',
      escales: 0,
      prixBase: 22.0,
      prixPromo: 18.5,
      prixEffectif: 18.5,
      pourcentageReduction: 16,
      enPromotion: true,
      placesDisponibles: 62,
      noteMoyenne: 4.6,
      nombreAvis: 48,
      nombreConsultations: 285,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: true,
      statut: 'DISPONIBLE'
    },
    {
      id: 20,
      codeVoyage: 'VOY-TUN-TOZ-02',
      titre: 'Vol Direct Oasis Tunis → Tozeur Nefta',
      description: 'Découvrez le Sud tunisien et les oasis du Sahara au départ de Tunis.',
      villeDepart: 'Tunis',
      villeDestination: 'Tozeur',
      paysDestination: 'Tunisie',
      compagnie: 'Tunisair Express',
      typeTransport: 'VOL',
      categorie: 'PROMO',
      dateDepart: '2026-11-22T10:00:00',
      dateArrivee: '2026-11-22T11:05:00',
      dureeMinutes: 65,
      dureeFormatee: '1h 05m',
      escales: 0,
      prixBase: 120.0,
      prixPromo: 89.0,
      prixEffectif: 89.0,
      pourcentageReduction: 26,
      enPromotion: true,
      placesDisponibles: 12,
      noteMoyenne: 4.9,
      nombreAvis: 29,
      nombreConsultations: 310,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      statut: 'DISPONIBLE'
    },
    {
      id: 23,
      codeVoyage: 'VOY-TUN-TAB-06',
      titre: 'Autocar Confort Montagne Tunis → Tabarka',
      description: 'Voyage panoramique climatisé vers la côte de corail et les forêts de chênes-lièges.',
      villeDepart: 'Tunis',
      villeDestination: 'Tabarka',
      paysDestination: 'Tunisie',
      compagnie: 'SNTRI',
      typeTransport: 'BUS',
      categorie: 'ECONOMIQUE',
      dateDepart: '2026-11-23T06:45:00',
      dateArrivee: '2026-11-23T09:30:00',
      dureeMinutes: 165,
      dureeFormatee: '2h 45m',
      escales: 1,
      prixBase: 17.0,
      prixPromo: 17.0,
      prixEffectif: 17.0,
      pourcentageReduction: 0,
      enPromotion: false,
      placesDisponibles: 35,
      noteMoyenne: 4.4,
      nombreAvis: 19,
      nombreConsultations: 95,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      statut: 'DISPONIBLE'
    }
  ];

  constructor(
    private rechercheService: RechercheConsultationService,
    private bagageService: BagageService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.cdr.detectChanges();
    });
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = null;

    // 1. Récupération des statistiques du catalogue (Module 2)
    this.rechercheService.getStatistiques().subscribe({
      next: (stats) => {
        this.statsCatalogue = stats;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.warn('Statistiques catalogue non disponibles:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });

    // 2. Récupération des statistiques bagages (Module 1 Redis) - Uniquement pour profil Admin
    if (this.authService.isAdmin()) {
      this.bagageService.getStatistics().subscribe({
        next: (stats) => {
          this.statsBagages = stats;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.warn('Statistiques bagages non disponibles:', err);
        }
      });
    } else {
      this.statsBagages = {
        totalBagages: 1,
        totalRevenue: 30.0,
        averageWeight: 8.5,
        totalWeight: 8.5,
        pendingBagages: 0,
        registeredBagages: 1
      };
    }

    // 3. Récupération des offres réelles pour la vitrine
    this.rechercheService.rechercherVoyages({}).subscribe({
      next: (voyages) => {
        if (voyages && voyages.length > 0) {
          this.featuredVoyages = voyages.slice(0, 4);
        } else {
          this.featuredVoyages = this.defaultFeaturedVoyages;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.featuredVoyages = this.defaultFeaturedVoyages;
        this.cdr.detectChanges();
      }
    });
  }
}
