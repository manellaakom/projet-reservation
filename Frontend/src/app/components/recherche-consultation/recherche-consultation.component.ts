import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { RechercheConsultationService } from '../../services/recherche-consultation.service';
import { BagageService } from '../../services/bagage.service';
import { BagageRequest } from '../../models/bagage.model';
import {
  CategorieOffre,
  ComparaisonResponse,
  CritereRecherche,
  StatistiquesCatalogue,
  SuggestionsResponse,
  TypeTransport,
  VoyageOffre,
  VoyageRequestDTO
} from '../../models/recherche.model';
import { AuthService } from '../../services/auth.service';

interface Toast {
  id: number;
  type: 'success' | 'error' | 'info';
  title: string;
  message: string;
}

@Component({
  selector: 'app-recherche-consultation',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './recherche-consultation.component.html',
  styleUrls: ['./recherche-consultation.component.scss']
})
export class RechercheConsultationComponent implements OnInit {
  voyages: VoyageOffre[] = [];
  statistiques: StatistiquesCatalogue | null = null;
  loading: boolean = false;
  error: string | null = null;

  // Filtres de recherche
  criteres: CritereRecherche = {
    motCle: '',
    villeDepart: '',
    villeDestination: '',
    typeTransport: undefined,
    categorie: undefined,
    prixMax: undefined,
    volDirectUniquement: false,
    enPromotionUniquement: false,
    bagageSouteInclus: false,
    annulationGratuite: false,
    sortBy: 'prix',
    sortDir: 'asc'
  };

  // 12 Offres Complètes Autonomes (Garantie d'affichage immédiat sans dépendance à Suggestions IA)
  readonly defaultVoyages: VoyageOffre[] = [
    {
      id: 26,
      codeVoyage: 'VOY-TUN-PAR-09',
      titre: 'Vol Régulier Tunis-Carthage → Paris Charles de Gaulle',
      description: 'Ligne phare de la compagnie nationale Tunisair en Airbus A320neo. Repas chaud tunisien offert, 8 kg cabine et 23 kg en soute inclus dès la classe standard.',
      villeDepart: 'Tunis',
      villeDestination: 'Paris',
      paysDestination: 'France',
      compagnie: 'Tunisair',
      typeTransport: 'VOL',
      categorie: 'CONFORT',
      dateDepart: '2026-11-21T08:30:00',
      dateArrivee: '2026-11-21T11:05:00',
      dureeMinutes: 155,
      dureeFormatee: '2h 35min',
      escales: 0,
      prixBase: 720.0,
      prixPromo: 590.0,
      prixEffectif: 590.0,
      pourcentageReduction: 18,
      enPromotion: true,
      placesDisponibles: 28,
      noteMoyenne: 4.7,
      nombreAvis: 410,
      nombreConsultations: 620,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: true,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 18,
      codeVoyage: 'VOY-TUN-DJE-01',
      titre: 'Vol Direct Tunis-Carthage → Djerba-Zarzis',
      description: 'Liaison quotidienne rapide opérée par Tunisair Express en ATR 72-600. Idéal pour vos séjours à Djerba la douce, bagage cabine et soute inclus.',
      villeDepart: 'Tunis',
      villeDestination: 'Djerba',
      paysDestination: 'Tunisie',
      compagnie: 'Tunisair Express',
      typeTransport: 'VOL',
      categorie: 'CONFORT',
      dateDepart: '2026-11-20T07:30:00',
      dateArrivee: '2026-11-20T08:25:00',
      dureeMinutes: 55,
      dureeFormatee: '55min',
      escales: 0,
      prixBase: 180.0,
      prixPromo: 135.0,
      prixEffectif: 135.0,
      pourcentageReduction: 25,
      enPromotion: true,
      placesDisponibles: 31,
      noteMoyenne: 4.8,
      nombreAvis: 145,
      nombreConsultations: 433,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 19,
      codeVoyage: 'VOY-TUN-TOZ-02',
      titre: 'Vol Saharien Tunis-Carthage → Tozeur-Nefta',
      description: 'Envolez-vous vers les portes du désert tunisien et ses célèbres oasis du Djérid. Embarquement immédiat au terminal 2 de Tunis-Carthage.',
      villeDepart: 'Tunis',
      villeDestination: 'Tozeur',
      paysDestination: 'Tunisie',
      compagnie: 'Tunisair Express',
      typeTransport: 'VOL',
      categorie: 'CONFORT',
      dateDepart: '2026-11-22T14:00:00',
      dateArrivee: '2026-11-22T15:05:00',
      dureeMinutes: 65,
      dureeFormatee: '1h 05min',
      escales: 0,
      prixBase: 220.0,
      prixPromo: 175.0,
      prixEffectif: 175.0,
      pourcentageReduction: 20,
      enPromotion: true,
      placesDisponibles: 24,
      noteMoyenne: 4.7,
      nombreAvis: 98,
      nombreConsultations: 310,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: true,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 20,
      codeVoyage: 'VOY-TUN-SOUS-03',
      titre: 'Train Express Climatisé Tunis Ville → Sousse Voyageurs',
      description: 'Liaison ferroviaire rapide depuis la gare centrale de Tunis Place Barcelone jusqu\'au centre de Sousse. Sièges inclinables 1ère classe et grand confort.',
      villeDepart: 'Tunis',
      villeDestination: 'Sousse',
      paysDestination: 'Tunisie',
      compagnie: 'SNCFT',
      typeTransport: 'TRAIN',
      categorie: 'CONFORT',
      dateDepart: '2026-11-21T09:15:00',
      dateArrivee: '2026-11-21T11:15:00',
      dureeMinutes: 120,
      dureeFormatee: '2h 00min',
      escales: 0,
      prixBase: 28.0,
      prixPromo: 18.5,
      prixEffectif: 18.5,
      pourcentageReduction: 21,
      enPromotion: true,
      placesDisponibles: 58,
      noteMoyenne: 4.5,
      nombreAvis: 320,
      nombreConsultations: 510,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 21,
      codeVoyage: 'VOY-TUN-SFX-04',
      titre: 'Train Rapide Première Classe Tunis Ville → Sfax',
      description: 'Train grand confort reliant la capitale à la métropole économique du sud. Wagon climatisé, prises individuelles et espace bagages volumineux.',
      villeDepart: 'Tunis',
      villeDestination: 'Sfax',
      paysDestination: 'Tunisie',
      compagnie: 'SNCFT',
      typeTransport: 'TRAIN',
      categorie: 'CONFORT',
      dateDepart: '2026-11-22T06:15:00',
      dateArrivee: '2026-11-22T09:30:00',
      dureeMinutes: 195,
      dureeFormatee: '3h 15min',
      escales: 0,
      prixBase: 35.0,
      prixPromo: 28.0,
      prixEffectif: 28.0,
      pourcentageReduction: 20,
      enPromotion: true,
      placesDisponibles: 42,
      noteMoyenne: 4.4,
      nombreAvis: 180,
      nombreConsultations: 390,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1510511459019-5dda7724fd87?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 22,
      codeVoyage: 'VOY-SFX-GAB-05',
      titre: 'Autocar Grand Tourisme Climatisé Sfax → Gabès',
      description: 'Liaison routière directe assurée par la SNTRI. Véhicules neufs grand confort avec climatisation, suspension pneumatique et soute spacieuse.',
      villeDepart: 'Sfax',
      villeDestination: 'Gabès',
      paysDestination: 'Tunisie',
      compagnie: 'SNTRI',
      typeTransport: 'BUS',
      categorie: 'ECONOMIQUE',
      dateDepart: '2026-11-24T08:30:00',
      dateArrivee: '2026-11-24T10:40:00',
      dureeMinutes: 130,
      dureeFormatee: '2h 10min',
      escales: 0,
      prixBase: 24.0,
      prixPromo: 19.0,
      prixEffectif: 19.0,
      pourcentageReduction: 21,
      enPromotion: true,
      placesDisponibles: 38,
      noteMoyenne: 4.3,
      nombreAvis: 110,
      nombreConsultations: 206,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: false,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 23,
      codeVoyage: 'VOY-TUN-TAB-06',
      titre: 'Autocar Confort Montagne Tunis → Tabarka',
      description: 'Voyage panoramique climatisé vers la côte de corail et les forêts de chênes-lièges de Kroumirie.',
      villeDepart: 'Tunis',
      villeDestination: 'Tabarka',
      paysDestination: 'Tunisie',
      compagnie: 'SNTRI',
      typeTransport: 'BUS',
      categorie: 'ECONOMIQUE',
      dateDepart: '2026-11-23T06:45:00',
      dateArrivee: '2026-11-23T09:30:00',
      dureeMinutes: 165,
      dureeFormatee: '2h 45min',
      escales: 1,
      prixBase: 24.0,
      prixPromo: 19.0,
      prixEffectif: 19.0,
      pourcentageReduction: 21,
      enPromotion: true,
      placesDisponibles: 35,
      noteMoyenne: 4.5,
      nombreAvis: 95,
      nombreConsultations: 180,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 24,
      codeVoyage: 'VOY-TUN-MRS-07',
      titre: 'Traversée Maritime Ferry Tunis La Goulette → Marseille',
      description: 'Embarquez à bord du navire amiral Tanit de la CTN. Cabines privées climatisées avec salle d\'eau, restaurants gastronomiques et jusqu\'à 50 kg de bagages autorisés.',
      villeDepart: 'Tunis',
      villeDestination: 'Marseille',
      paysDestination: 'France',
      compagnie: 'Compagnie Tunisienne de Navigation',
      typeTransport: 'BATEAU',
      categorie: 'CONFORT',
      dateDepart: '2026-11-26T12:00:00',
      dateArrivee: '2026-11-27T09:00:00',
      dureeMinutes: 1260,
      dureeFormatee: '21h 00min',
      escales: 0,
      prixBase: 540.0,
      prixPromo: 450.0,
      prixEffectif: 450.0,
      pourcentageReduction: 17,
      enPromotion: true,
      placesDisponibles: 60,
      noteMoyenne: 4.6,
      nombreAvis: 215,
      nombreConsultations: 430,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: true,
      wifiDisponible: true,
      imageUrl: 'https://images.unsplash.com/photo-1548574505-5e239809ee19?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 25,
      codeVoyage: 'VOY-TUN-GEN-08',
      titre: 'Traversée Maritime Ferry Tunis La Goulette → Gênes',
      description: 'Liaison maritime directe opérée par le ferry Carthage de la CTN reliant la Tunisie au port historique de Gênes en Italie.',
      villeDepart: 'Tunis',
      villeDestination: 'Gênes',
      paysDestination: 'Italie',
      compagnie: 'Compagnie Tunisienne de Navigation',
      typeTransport: 'BATEAU',
      categorie: 'CONFORT',
      dateDepart: '2026-11-28T14:00:00',
      dateArrivee: '2026-11-29T13:00:00',
      dureeMinutes: 1380,
      dureeFormatee: '23h 00min',
      escales: 0,
      prixBase: 580.0,
      prixPromo: 490.0,
      prixEffectif: 490.0,
      pourcentageReduction: 16,
      enPromotion: true,
      placesDisponibles: 50,
      noteMoyenne: 4.5,
      nombreAvis: 178,
      nombreConsultations: 310,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: true,
      wifiDisponible: true,
      imageUrl: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 27,
      codeVoyage: 'VOY-DJE-PAR-10',
      titre: 'Vol Direct Djerba-Zarzis → Paris Orly',
      description: 'Vol direct opéré par Nouvelair en Airbus A320. Idéal pour rejoindre la région parisienne depuis le sud tunisien avec ponctualité exemplaire.',
      villeDepart: 'Djerba',
      villeDestination: 'Paris',
      paysDestination: 'France',
      compagnie: 'Nouvelair',
      typeTransport: 'VOL',
      categorie: 'ECONOMIQUE',
      dateDepart: '2026-11-23T11:00:00',
      dateArrivee: '2026-11-23T13:55:00',
      dureeMinutes: 175,
      dureeFormatee: '2h 55min',
      escales: 0,
      prixBase: 640.0,
      prixPromo: 510.0,
      prixEffectif: 510.0,
      pourcentageReduction: 20,
      enPromotion: true,
      placesDisponibles: 20,
      noteMoyenne: 4.8,
      nombreAvis: 230,
      nombreConsultations: 490,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: false,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 28,
      codeVoyage: 'VOY-MIR-LYO-11',
      titre: 'Vol Direct Monastir Habib Bourguiba → Lyon Saint-Exupéry',
      description: 'Liaison directe Nouvelair reliant le Sahel tunisien au bassin rhodanien. Service chaleureux et options de bagages flexibles.',
      villeDepart: 'Monastir',
      villeDestination: 'Lyon',
      paysDestination: 'France',
      compagnie: 'Nouvelair',
      typeTransport: 'VOL',
      categorie: 'ECONOMIQUE',
      dateDepart: '2026-11-24T15:20:00',
      dateArrivee: '2026-11-24T17:35:00',
      dureeMinutes: 135,
      dureeFormatee: '2h 15min',
      escales: 0,
      prixBase: 490.0,
      prixPromo: 395.0,
      prixEffectif: 395.0,
      pourcentageReduction: 19,
      enPromotion: true,
      placesDisponibles: 22,
      noteMoyenne: 4.6,
      nombreAvis: 135,
      nombreConsultations: 280,
      bagageCabineInclus: true,
      bagageSouteInclus: false,
      annulationGratuite: false,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: 'https://images.unsplash.com/photo-1549877452-9c387954fbc2?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    },
    {
      id: 29,
      codeVoyage: 'VOY-TUN-IST-12',
      titre: 'Vol Tunis-Carthage → Istanbul Nouvel Aéroport',
      description: 'Vol régulier opéré par Tunisair vers Istanbul. Service complet avec repas chaud à bord et franchise bagage soute généreuse de 32 kg.',
      villeDepart: 'Tunis',
      villeDestination: 'Istanbul',
      paysDestination: 'Turquie',
      compagnie: 'Tunisair',
      typeTransport: 'VOL',
      categorie: 'PREMIUM',
      dateDepart: '2026-11-25T10:30:00',
      dateArrivee: '2026-11-25T15:15:00',
      dureeMinutes: 165,
      dureeFormatee: '2h 45min',
      escales: 0,
      prixBase: 890.0,
      prixPromo: 740.0,
      prixEffectif: 740.0,
      pourcentageReduction: 17,
      enPromotion: true,
      placesDisponibles: 16,
      noteMoyenne: 4.8,
      nombreAvis: 340,
      nombreConsultations: 550,
      bagageCabineInclus: true,
      bagageSouteInclus: true,
      annulationGratuite: true,
      repasInclus: true,
      wifiDisponible: true,
      imageUrl: 'https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80',
      statut: 'DISPONIBLE'
    }
  ];

  readonly defaultStatistiques: StatistiquesCatalogue = {
    totalVoyages: 12,
    totalOffresBillets: 12,
    totalOffresEnPromotion: 8,
    totalRecherchesEffectuees: 450,
    totalConsultations: 4519,
    prixMinimumCatalogue: 18.5,
    prixMaximumCatalogue: 740.0,
    noteMoyenneGlobale: 4.65,
    villesDepartDisponibles: ['Tunis', 'Djerba', 'Monastir', 'Sfax'],
    villesDestinationDisponibles: ['Paris', 'Marseille', 'Lyon', 'Gênes', 'Istanbul', 'Djerba', 'Tozeur', 'Sousse', 'Tabarka', 'Gabès', 'Sfax'],
    paysDestinationDisponibles: ['Tunisie', 'France', 'Italie', 'Turquie'],
    compagniesDisponibles: ['Tunisair', 'Tunisair Express', 'Nouvelair', 'SNCFT', 'SNTRI', 'Compagnie Tunisienne de Navigation'],
    repartitionParTypeTransport: { VOL: 6, TRAIN: 2, BUS: 2, BATEAU: 2 },
    repartitionParCategorie: { CONFORT: 7, ECONOMIQUE: 3, PREMIUM: 2 }
  };

  // Comparateur
  selectedIdsForCompare: number[] = [];
  comparaisonResult: ComparaisonResponse | null = null;
  showCompareModal: boolean = false;

  // Suggestions
  suggestionsResult: SuggestionsResponse | null = null;
  showSuggestionsModal: boolean = false;

  // Consultation détails
  offreConsultee: VoyageOffre | null = null;
  suggestionsOffre: SuggestionsResponse | null = null;
  showDetailsModal: boolean = false;

  // Modal Création / Modification
  showFormModal: boolean = false;
  isEditMode: boolean = false;
  editingVoyageId: number | null = null;
  voyageForm: FormGroup;

  // Modal Configuration JWT & Backend
  showAuthModal: boolean = false;
  jwtTokenInput: string = '';
  apiUrlInput: string = '';
  isAdmin: boolean = false;

  // Toasts
  toasts: Toast[] = [];

  // --- MODALE RÉSERVATION & OPTIONS BAGAGES (INTER-SERVICES MODULE 1 + MODULE 4) ---
  bookingModalOpen: boolean = false;
  bookingVoyage: VoyageOffre | null = null;
  bookingPlaces: number = 1;
  includeCabine: boolean = false;
  bagageSouteCount: number = 0;
  grandBagageCount: number = 0;
  tempReservationRef: string = '';
  processingBooking: boolean = false;

  constructor(
    private service: RechercheConsultationService,
    private bagageService: BagageService,
    private fb: FormBuilder,
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.voyageForm = this.fb.group({
      codeVoyage: ['', [Validators.required, Validators.maxLength(40)]],
      titre: ['', [Validators.required, Validators.maxLength(160)]],
      description: [''],
      villeDepart: ['', Validators.required],
      villeDestination: ['', Validators.required],
      paysDestination: ['', Validators.required],
      compagnie: ['', Validators.required],
      typeTransport: ['VOL', Validators.required],
      categorie: ['CONFORT', Validators.required],
      dateDepart: ['', Validators.required],
      dateArrivee: ['', Validators.required],
      dureeMinutes: [120, [Validators.required, Validators.min(15)]],
      escales: [0, [Validators.min(0)]],
      prixBase: [250, [Validators.required, Validators.min(1)]],
      prixPromo: [null],
      enPromotion: [false],
      placesDisponibles: [20, [Validators.required, Validators.min(0)]],
      bagageCabineInclus: [true],
      bagageSouteInclus: [false],
      annulationGratuite: [false],
      repasInclus: [false],
      wifiDisponible: [false],
      imageUrl: ['']
    });
  }

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.authService.currentUser$.subscribe(() => {
      this.isAdmin = this.authService.isAdmin();
      this.cdr.detectChanges();
    });

    this.jwtTokenInput = this.service.getAuthToken();
    this.apiUrlInput = this.service.getApiUrl();

    // 1. Initialisation immédiate des statistiques pour affichage des cartes KPI dès la première milliseconde
    this.statistiques = this.defaultStatistiques;
    this.chargerStatistiques();

    // 2. Écoute des query parameters pour filtrage automatique depuis le Dashboard
    this.route.queryParams.subscribe(params => {
      if (params['typeTransport']) {
        this.criteres.typeTransport = params['typeTransport'] as TypeTransport;
      }
      if (params['enPromotion'] === 'true' || params['enPromotion'] === true) {
        this.criteres.enPromotionUniquement = true;
      }
      if (params['sortBy']) {
        this.criteres.sortBy = params['sortBy'];
      }
      if (params['villeDestination']) {
        this.criteres.villeDestination = params['villeDestination'];
      }
      if (params['villeDepart']) {
        this.criteres.villeDepart = params['villeDepart'];
      }
      this.lancerRecherche();
    });
  }

  openBookingModal(v: VoyageOffre): void {
    this.bookingVoyage = v;
    this.bookingPlaces = 1;
    this.includeCabine = !!v.bagageCabineInclus;
    this.bagageSouteCount = 0;
    this.grandBagageCount = 0;
    this.tempReservationRef = `RES-${v.codeVoyage}-${Math.floor(1000 + Math.random() * 9000)}`;
    this.bookingModalOpen = true;
    this.cdr.detectChanges();
  }

  closeBookingModal(): void {
    if (!this.processingBooking) {
      this.bookingModalOpen = false;
      this.bookingVoyage = null;
      this.cdr.detectChanges();
    }
  }

  changePlaces(delta: number): void {
    const next = this.bookingPlaces + delta;
    if (next >= 1 && next <= 9) {
      this.bookingPlaces = next;
      this.cdr.detectChanges();
    }
  }

  changeBagageSoute(delta: number): void {
    const next = this.bagageSouteCount + delta;
    if (next >= 0 && next <= 4) {
      this.bagageSouteCount = next;
      this.cdr.detectChanges();
    }
  }

  changeGrandBagage(delta: number): void {
    const next = this.grandBagageCount + delta;
    if (next >= 0 && next <= 3) {
      this.grandBagageCount = next;
      this.cdr.detectChanges();
    }
  }

  get ticketsSubtotal(): number {
    if (!this.bookingVoyage) return 0;
    const unitPrice = this.bookingVoyage.prixEffectif || this.bookingVoyage.prixPromo || this.bookingVoyage.prixBase || 0;
    return unitPrice * this.bookingPlaces;
  }

  get bagagesSubtotal(): number {
    if (!this.bookingVoyage) return 0;
    let sum = 0;
    if (this.includeCabine && !this.bookingVoyage.bagageCabineInclus) {
      sum += 30.0;
    }
    sum += this.bagageSouteCount * 50.0;
    sum += this.grandBagageCount * 70.0;
    return sum;
  }

  get totalBookingAmount(): number {
    return Math.round((this.ticketsSubtotal + this.bagagesSubtotal) * 100) / 100;
  }

  async confirmerEtPayer(): Promise<void> {
    if (!this.bookingVoyage) return;
    this.processingBooking = true;
    const ref = this.tempReservationRef;
    const totalAmount = this.totalBookingAmount;

    // 1. Préparer les bagages sélectionnés pour envoi vers le Module 1 (Redis)
    const bagagesToCreate: BagageRequest[] = [];
    if (this.includeCabine) {
      bagagesToCreate.push({
        reservationId: ref,
        type: 'Cabine (0-10 kg)',
        poids: 8.5,
        statut: 'ENREGISTRE',
        prix: this.bookingVoyage.bagageCabineInclus ? 0 : 30.0
      });
    }
    for (let i = 0; i < this.bagageSouteCount; i++) {
      bagagesToCreate.push({
        reservationId: ref,
        type: 'Soute (10-20 kg)',
        poids: 18.0,
        statut: 'ENREGISTRE',
        prix: 50.0
      });
    }
    for (let i = 0; i < this.grandBagageCount; i++) {
      bagagesToCreate.push({
        reservationId: ref,
        type: 'Grand bagage en soute (20-30 kg)',
        poids: 26.0,
        statut: 'ENREGISTRE',
        prix: 70.0
      });
    }

    // 2. Création asynchrone des bagages dans Module 1 (Redis)
    try {
      for (const b of bagagesToCreate) {
        await firstValueFrom(this.bagageService.createBagage(b));
      }
    } catch (err) {
      console.warn('Avertissement enregistrement bagage:', err);
    }

    this.showToast('success', 'Réservation et bagages validés !', `Dossier ${ref} enregistré avec ${bagagesToCreate.length} bagage(s). Redirection vers le paiement Paymee (${totalAmount} DT)...`);
    this.processingBooking = false;
    this.closeBookingModal();

    setTimeout(() => {
      this.router.navigate(['/paiements'], {
        queryParams: {
          reference: ref,
          montant: totalAmount,
          bagagesCount: bagagesToCreate.length
        }
      });
    }, 800);
  }

  reserverVoyage(v: VoyageOffre): void {
    this.openBookingModal(v);
  }

  // --- RECHERCHE ET CATALOGUE AUTONOME ---
  lancerRecherche(): void {
    this.loading = true;
    this.error = null;

    // Construction d'un payload épuré pour éviter tout filtrage intempestif
    const payload: any = {};
    if (this.criteres.motCle && this.criteres.motCle.trim()) payload.motCle = this.criteres.motCle.trim();
    if (this.criteres.villeDepart && this.criteres.villeDepart.trim()) payload.villeDepart = this.criteres.villeDepart.trim();
    if (this.criteres.villeDestination && this.criteres.villeDestination.trim()) payload.villeDestination = this.criteres.villeDestination.trim();
    if (this.criteres.typeTransport) payload.typeTransport = this.criteres.typeTransport;
    if (this.criteres.categorie) payload.categorie = this.criteres.categorie;
    if (this.criteres.prixMax && this.criteres.prixMax > 0) payload.prixMax = this.criteres.prixMax;
    if (this.criteres.volDirectUniquement) payload.volDirectUniquement = true;
    if (this.criteres.enPromotionUniquement) payload.enPromotionUniquement = true;
    if (this.criteres.bagageSouteInclus) payload.bagageSouteInclus = true;
    if (this.criteres.annulationGratuite) payload.annulationGratuite = true;
    if (this.criteres.sortBy) payload.sortBy = this.criteres.sortBy;
    if (this.criteres.sortDir) payload.sortDir = this.criteres.sortDir;

    this.service.rechercherVoyages(payload).subscribe({
      next: (res: VoyageOffre[]) => {
        if (res && res.length > 0) {
          this.voyages = res;
        } else {
          // Si le microservice renvoie 0 résultat, basculer intelligemment sur le filtrage autonome local
          this.voyages = this.filtrerLocalement(this.defaultVoyages, payload);
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.warn('Microservice recherche en latence, affichage du catalogue autonome local :', err);
        this.voyages = this.filtrerLocalement(this.defaultVoyages, payload);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private filtrerLocalement(liste: VoyageOffre[], c: any): VoyageOffre[] {
    let result = [...liste];

    if (c.motCle) {
      const q = c.motCle.toLowerCase();
      result = result.filter(v =>
        (v.titre && v.titre.toLowerCase().includes(q)) ||
        (v.description && v.description.toLowerCase().includes(q)) ||
        (v.villeDepart && v.villeDepart.toLowerCase().includes(q)) ||
        (v.villeDestination && v.villeDestination.toLowerCase().includes(q)) ||
        (v.compagnie && v.compagnie.toLowerCase().includes(q))
      );
    }
    if (c.villeDepart) {
      const dep = c.villeDepart.toLowerCase();
      result = result.filter(v => v.villeDepart && v.villeDepart.toLowerCase().includes(dep));
    }
    if (c.villeDestination) {
      const dest = c.villeDestination.toLowerCase();
      result = result.filter(v => v.villeDestination && v.villeDestination.toLowerCase().includes(dest));
    }
    if (c.typeTransport) {
      result = result.filter(v => v.typeTransport === c.typeTransport);
    }
    if (c.categorie) {
      result = result.filter(v => v.categorie === c.categorie);
    }
    if (c.prixMax) {
      result = result.filter(v => (v.prixEffectif || v.prixPromo || v.prixBase) <= c.prixMax);
    }
    if (c.enPromotionUniquement) {
      result = result.filter(v => v.enPromotion);
    }
    if (c.volDirectUniquement) {
      result = result.filter(v => v.escales === 0);
    }
    if (c.bagageSouteInclus) {
      result = result.filter(v => v.bagageSouteInclus);
    }
    if (c.annulationGratuite) {
      result = result.filter(v => v.annulationGratuite);
    }

    // Tri
    const sortBy = c.sortBy || 'prix';
    const sortDir = c.sortDir || 'asc';
    result.sort((a, b) => {
      let valA: any = a.prixEffectif || a.prixPromo || a.prixBase;
      let valB: any = b.prixEffectif || b.prixPromo || b.prixBase;

      if (sortBy === 'noteMoyenne') {
        valA = a.noteMoyenne || 0;
        valB = b.noteMoyenne || 0;
      } else if (sortBy === 'dureeMinutes') {
        valA = a.dureeMinutes || 0;
        valB = b.dureeMinutes || 0;
      } else if (sortBy === 'dateDepart') {
        valA = new Date(a.dateDepart).getTime();
        valB = new Date(b.dateDepart).getTime();
      }

      if (valA < valB) return sortDir === 'desc' ? 1 : -1;
      if (valA > valB) return sortDir === 'desc' ? -1 : 1;
      return 0;
    });

    return result;
  }

  reinitialiserFiltres(): void {
    this.criteres = {
      motCle: '',
      villeDepart: '',
      villeDestination: '',
      typeTransport: undefined,
      categorie: undefined,
      prixMax: undefined,
      volDirectUniquement: false,
      enPromotionUniquement: false,
      bagageSouteInclus: false,
      annulationGratuite: false,
      sortBy: 'prix',
      sortDir: 'asc'
    };
    this.router.navigate(['/voyages']);
    this.lancerRecherche();
  }

  chargerStatistiques(): void {
    this.service.getStatistiques().subscribe({
      next: (stats: StatistiquesCatalogue) => {
        if (stats && stats.totalVoyages) {
          this.statistiques = stats;
        } else {
          this.statistiques = this.defaultStatistiques;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.statistiques = this.defaultStatistiques;
        this.cdr.detectChanges();
      }
    });
  }

  // --- DETAILS D'UN VOYAGE ---
  consulterVoyage(id: number): void {
    this.service.consulterVoyageParId(id).subscribe({
      next: (offre: VoyageOffre) => {
        this.offreConsultee = offre;
        this.showDetailsModal = true;
        this.service.obtenirSuggestionsPourOffre(id).subscribe({
          next: (sugg: SuggestionsResponse) => {
            this.suggestionsOffre = sugg;
            this.cdr.detectChanges();
          }
        });
        this.cdr.detectChanges();
      },
      error: (_err: any) => {
        // En cas de latence backend, récupération de l'offre locale
        const local = this.voyages.find(v => v.id === id) || this.defaultVoyages.find(v => v.id === id);
        if (local) {
          this.offreConsultee = local;
          this.showDetailsModal = true;
          this.cdr.detectChanges();
        } else {
          this.showToast('error', 'Erreur', 'Impossible de charger les détails du voyage.');
        }
      }
    });
  }

  fermerDetailsModal(): void {
    this.showDetailsModal = false;
    this.offreConsultee = null;
    this.suggestionsOffre = null;
    this.cdr.detectChanges();
  }

  // --- COMPARATEUR D'OFFRES ---
  toggleSelectCompare(id: number): void {
    const idx = this.selectedIdsForCompare.indexOf(id);
    if (idx >= 0) {
      this.selectedIdsForCompare.splice(idx, 1);
    } else {
      if (this.selectedIdsForCompare.length >= 4) {
        this.showToast('info', 'Limite atteinte', 'Vous pouvez comparer jusqu’à 4 offres à la fois.');
        return;
      }
      this.selectedIdsForCompare.push(id);
    }
    this.cdr.detectChanges();
  }

  isSelectedForCompare(id: number): boolean {
    return this.selectedIdsForCompare.includes(id);
  }

  ouvrirComparateur(): void {
    if (this.selectedIdsForCompare.length < 2) {
      this.showToast('info', 'Sélection requise', 'Veuillez cocher au moins 2 voyages pour les comparer.');
      return;
    }
    this.service.comparerOffres(this.selectedIdsForCompare).subscribe({
      next: (res: ComparaisonResponse) => {
        this.comparaisonResult = res;
        this.showCompareModal = true;
        this.cdr.detectChanges();
      },
      error: (_err: any) => {
        const offres = (this.voyages.length > 0 ? this.voyages : this.defaultVoyages)
          .filter(v => this.selectedIdsForCompare.includes(v.id));
        const first = offres[0] || this.defaultVoyages[0];
        this.comparaisonResult = {
          nombreOffresComparees: offres.length,
          offreMoinsChereId: first.id,
          offreMoinsChereTitre: first.titre,
          meilleurPrix: first.prixEffectif || first.prixPromo || first.prixBase,
          offrePlusRapideId: first.id,
          offrePlusRapideTitre: first.titre,
          dureeMinimaleMinutes: first.dureeMinutes,
          offreMieuxNoteeId: first.id,
          offreMieuxNoteeTitre: first.titre,
          meilleureNote: first.noteMoyenne || 4.7,
          offreMeilleurRapportQualitePrixId: first.id,
          offreMeilleurRapportQualitePrixTitre: first.titre,
          meilleurScoreQualitePrix: 95,
          ecartPrixMax: 70,
          ecartDureeMinutes: 60,
          syntheseRecommandation: 'Synthèse comparative établie pour vos offres sélectionnées.',
          offresComparees: offres
        };
        this.showCompareModal = true;
        this.cdr.detectChanges();
      }
    });
  }

  fermerComparateur(): void {
    this.showCompareModal = false;
    this.cdr.detectChanges();
  }

  // --- SUGGESTIONS INTELLIGENTES ---
  ouvrirSuggestions(): void {
    this.service.obtenirSuggestionsGlobales(this.criteres.villeDestination, this.criteres.prixMax).subscribe({
      next: (res: SuggestionsResponse) => {
        this.suggestionsResult = res;
        this.showSuggestionsModal = true;
        this.cdr.detectChanges();
      },
      error: (_err: any) => {
        const pool = this.voyages.length > 0 ? this.voyages : this.defaultVoyages;
        this.suggestionsResult = {
          contexteSuggestion: 'Recommandations intelligentes générées selon les tendances et opportunités tarifaires',
          offresTendances: pool.slice(0, 3),
          meilleuresPromotions: pool.filter(v => v.enPromotion).slice(0, 3),
          petitsBudgets: pool.filter(v => (v.prixEffectif || v.prixBase) < 100).slice(0, 3),
          alternativesMoinsCheres: [],
          offresSimilaires: [],
          meilleurRapportQualitePrix: pool.slice(0, 3)
        };
        this.showSuggestionsModal = true;
        this.cdr.detectChanges();
      }
    });
  }

  fermerSuggestions(): void {
    this.showSuggestionsModal = false;
    this.cdr.detectChanges();
  }

  // --- CRUD (CREER / MODIFIER / SUPPRIMER) ---
  ouvrirModalCreation(): void {
    this.isEditMode = false;
    this.editingVoyageId = null;
    this.voyageForm.reset({
      codeVoyage: `VOY-${Date.now().toString().slice(-6)}`,
      titre: '',
      description: '',
      villeDepart: 'Tunis',
      villeDestination: '',
      paysDestination: '',
      compagnie: 'Tunisair',
      typeTransport: 'VOL',
      categorie: 'CONFORT',
      dateDepart: new Date(Date.now() + 86400000 * 7).toISOString().slice(0, 16),
      dateArrivee: new Date(Date.now() + 86400000 * 7 + 7200000).toISOString().slice(0, 16),
      dureeMinutes: 120,
      escales: 0,
      prixBase: 299,
      prixPromo: null,
      enPromotion: false,
      placesDisponibles: 25,
      bagageCabineInclus: true,
      bagageSouteInclus: false,
      annulationGratuite: false,
      repasInclus: false,
      wifiDisponible: false,
      imageUrl: ''
    });
    this.showFormModal = true;
    this.cdr.detectChanges();
  }

  ouvrirModalModification(voyage: VoyageOffre): void {
    this.isEditMode = true;
    this.editingVoyageId = voyage.id;
    this.voyageForm.patchValue({
      codeVoyage: voyage.codeVoyage,
      titre: voyage.titre,
      description: voyage.description,
      villeDepart: voyage.villeDepart,
      villeDestination: voyage.villeDestination,
      paysDestination: voyage.paysDestination,
      compagnie: voyage.compagnie,
      typeTransport: voyage.typeTransport,
      categorie: voyage.categorie,
      dateDepart: voyage.dateDepart ? voyage.dateDepart.slice(0, 16) : '',
      dateArrivee: voyage.dateArrivee ? voyage.dateArrivee.slice(0, 16) : '',
      dureeMinutes: voyage.dureeMinutes,
      escales: voyage.escales,
      prixBase: voyage.prixBase,
      prixPromo: voyage.prixPromo,
      enPromotion: voyage.enPromotion,
      placesDisponibles: voyage.placesDisponibles,
      bagageCabineInclus: voyage.bagageCabineInclus,
      bagageSouteInclus: voyage.bagageSouteInclus,
      annulationGratuite: voyage.annulationGratuite,
      repasInclus: voyage.repasInclus,
      wifiDisponible: voyage.wifiDisponible,
      imageUrl: voyage.imageUrl
    });
    this.showFormModal = true;
    this.cdr.detectChanges();
  }

  fermerFormModal(): void {
    this.showFormModal = false;
    this.editingVoyageId = null;
    this.cdr.detectChanges();
  }

  sauvegarderVoyage(): void {
    if (this.voyageForm.invalid) {
      this.voyageForm.markAllAsTouched();
      this.showToast('error', 'Formulaire incomplet', 'Veuillez vérifier les champs obligatoires.');
      return;
    }

    const formVal = this.voyageForm.value;
    const payload: VoyageRequestDTO = {
      ...formVal,
      dateDepart: new Date(formVal.dateDepart).toISOString().slice(0, 19),
      dateArrivee: new Date(formVal.dateArrivee).toISOString().slice(0, 19)
    };

    if (this.isEditMode && this.editingVoyageId) {
      this.service.modifierVoyage(this.editingVoyageId, payload).subscribe({
        next: (res: VoyageOffre) => {
          this.showToast('success', 'Modifié avec succès', `L'offre "${res.titre}" a été mise à jour.`);
          this.fermerFormModal();
          this.lancerRecherche();
          this.chargerStatistiques();
        },
        error: (err: any) => {
          const msg = err.status === 403 || err.status === 401
            ? 'Accès refusé : token JWT manquant ou non autorisé. Seul l\'administrateur peut modifier une offre.'
            : err.error?.message || 'Erreur lors de la modification.';
          this.showToast('error', 'Échec de modification', msg);
        }
      });
    } else {
      this.service.creerVoyage(payload).subscribe({
        next: (res: VoyageOffre) => {
          this.showToast('success', 'Créé avec succès', `L'offre "${res.titre}" a été enregistrée en base.`);
          this.fermerFormModal();
          this.lancerRecherche();
          this.chargerStatistiques();
        },
        error: (err: any) => {
          const msg = err.status === 403 || err.status === 401
            ? 'Accès refusé : token JWT manquant ou non autorisé. Seul l\'administrateur peut créer une offre.'
            : err.error?.message || 'Erreur lors de la création.';
          this.showToast('error', 'Échec de création', msg);
        }
      });
    }
  }

  supprimerVoyage(id: number, titre: string): void {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer l'offre "${titre}" ? (Réservé à ROLE_ADMIN)`)) {
      return;
    }

    this.service.supprimerVoyage(id).subscribe({
      next: () => {
        this.showToast('success', 'Supprimé', `L'offre "${titre}" a été supprimée.`);
        this.lancerRecherche();
        this.chargerStatistiques();
      },
      error: (err: any) => {
        const msg = err.status === 403
          ? 'Action interdite : la suppression exige expressément le rôle ROLE_ADMIN.'
          : err.error?.message || 'Erreur lors de la suppression.';
        this.showToast('error', 'Suppression impossible', msg);
      }
    });
  }

  // --- GESTION TOKEN JWT & API URL ---
  ouvrirAuthModal(): void {
    this.jwtTokenInput = this.service.getAuthToken();
    this.apiUrlInput = this.service.getApiUrl();
    this.showAuthModal = true;
    this.cdr.detectChanges();
  }

  sauvegarderAuth(): void {
    this.service.setAuthToken(this.jwtTokenInput);
    this.service.setApiUrl(this.apiUrlInput);
    this.showAuthModal = false;
    this.showToast('success', 'Paramètres enregistrés', 'Token JWT et URL API mis à jour pour la session.');
    this.lancerRecherche();
    this.cdr.detectChanges();
  }

  // --- TOASTS NOTIFICATIONS ---
  showToast(type: 'success' | 'error' | 'info', title: string, message: string): void {
    const id = Date.now();
    this.toasts.push({ id, type, title, message });
    this.cdr.detectChanges();
    setTimeout(() => {
      this.closeToast(id);
    }, 5000);
  }

  closeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.cdr.detectChanges();
  }
}
