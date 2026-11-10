import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RechercheConsultationService } from '../../services/recherche-consultation.service';
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

  // Toasts
  toasts: Toast[] = [];

  constructor(
    private service: RechercheConsultationService,
    private fb: FormBuilder
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
    this.jwtTokenInput = this.service.getAuthToken();
    this.apiUrlInput = this.service.getApiUrl();
    this.lancerRecherche();
    this.chargerStatistiques();
  }

  // --- RECHERCHE ET CATALOGUE ---
  lancerRecherche(): void {
    this.loading = true;
    this.error = null;

    // Nettoyage des critères vides
    const payload: CritereRecherche = { ...this.criteres };
    if (!payload.motCle) delete payload.motCle;
    if (!payload.villeDepart) delete payload.villeDepart;
    if (!payload.villeDestination) delete payload.villeDestination;
    if (!payload.typeTransport) delete payload.typeTransport;
    if (!payload.categorie) delete payload.categorie;
    if (!payload.prixMax) delete payload.prixMax;

    this.service.rechercherVoyages(payload).subscribe({
      next: (res: VoyageOffre[]) => {
        this.voyages = res;
        this.loading = false;
      },
      error: (err: any) => {
        this.loading = false;
        this.error = `Impossible de joindre le microservice sur ${this.service.getApiUrl()}. Vérifiez qu'il est bien démarré.`;
        this.showToast('error', 'Erreur de connexion', this.error);
      }
    });
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
    this.lancerRecherche();
  }

  chargerStatistiques(): void {
    this.service.getStatistiques().subscribe({
      next: (stats: StatistiquesCatalogue) => {
        this.statistiques = stats;
      },
      error: () => {}
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
          }
        });
      },
      error: (_err: any) => {
        this.showToast('error', 'Erreur', 'Impossible de charger les détails du voyage.');
      }
    });
  }

  fermerDetailsModal(): void {
    this.showDetailsModal = false;
    this.offreConsultee = null;
    this.suggestionsOffre = null;
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
      },
      error: (_err: any) => {
        this.showToast('error', 'Erreur', 'Impossible de comparer ces offres.');
      }
    });
  }

  fermerComparateur(): void {
    this.showCompareModal = false;
  }

  // --- SUGGESTIONS INTELLIGENTES ---
  ouvrirSuggestions(): void {
    this.service.obtenirSuggestionsGlobales(this.criteres.villeDestination, this.criteres.prixMax).subscribe({
      next: (res: SuggestionsResponse) => {
        this.suggestionsResult = res;
        this.showSuggestionsModal = true;
      },
      error: (_err: any) => {
        this.showToast('error', 'Erreur', 'Impossible de charger les suggestions.');
      }
    });
  }

  fermerSuggestions(): void {
    this.showSuggestionsModal = false;
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
  }

  fermerFormModal(): void {
    this.showFormModal = false;
    this.editingVoyageId = null;
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
            ? 'Accès refusé : token JWT manquant ou non autorisé. Renseignez un token dans "Configuration JWT".'
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
            ? 'Accès refusé : token JWT manquant ou invalide. Renseignez un token dans "Configuration JWT".'
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
          ? 'Action interdite : la suppression exige expressément le rôle ROLE_ADMIN. Renseignez le token Admin.'
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
  }

  sauvegarderAuth(): void {
    this.service.setAuthToken(this.jwtTokenInput);
    this.service.setApiUrl(this.apiUrlInput);
    this.showAuthModal = false;
    this.showToast('success', 'Paramètres enregistrés', 'Token JWT et URL API mis à jour pour la session.');
    this.lancerRecherche();
  }

  remplirTokenAdminDemo(): void {
    this.service.getKeycloakDemoTokens().subscribe({
      next: (res: any) => {
        this.jwtTokenInput = res.ADMIN_TOKEN;
        this.showToast('info', 'Token Keycloak Admin', 'Token Administrateur (ROLE_ADMIN) chargé.');
      },
      error: () => {
        this.jwtTokenInput = 'eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvcmVhbG1zL3RyYXZlbC1yZWFsbSIsInN1YiI6ImFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiUk9MRV9BRE1JTiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXX19';
      }
    });
  }

  remplirTokenClientDemo(): void {
    this.service.getKeycloakDemoTokens().subscribe({
      next: (res: any) => {
        this.jwtTokenInput = res.USER_TOKEN;
        this.showToast('info', 'Token Keycloak Voyageur', 'Token Voyageur (ROLE_USER) chargé.');
      },
      error: () => {
        this.jwtTokenInput = 'eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvcmVhbG1zL3RyYXZlbC1yZWFsbSIsInN1YiI6InZveWFnZXVyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidm95YWdldXIiLCJyb2xlIjoiUk9MRV9VU0VSIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlVTRVIiXX19';
      }
    });
  }

  connecterKeycloakAdmin(): void {
    this.service.loginKeycloak({ username: 'admin', password: 'admin' }).subscribe({
      next: (res: any) => {
        this.service.setAuthToken(res.token);
        this.jwtTokenInput = res.token;
        this.showToast('success', 'Connecté via Keycloak (Admin)', 'Privilèges complets accordés (Création, Édition, Suppression).');
      },
      error: () => {
        this.remplirTokenAdminDemo();
        this.service.setAuthToken(this.jwtTokenInput);
      }
    });
  }

  connecterKeycloakVoyageur(): void {
    this.service.loginKeycloak({ username: 'voyageur', password: '123' }).subscribe({
      next: (res: any) => {
        this.service.setAuthToken(res.token);
        this.jwtTokenInput = res.token;
        this.showToast('success', 'Connecté via Keycloak (Voyageur)', 'Droits de consultation et recherche accordés.');
      },
      error: () => {
        this.remplirTokenClientDemo();
        this.service.setAuthToken(this.jwtTokenInput);
      }
    });
  }

  deconnecter(): void {
    this.service.setAuthToken('');
    this.jwtTokenInput = '';
    this.showToast('info', 'Déconnecté', 'Session réinitialisée en mode anonyme.');
  }

  basculerVersDocker(): void {
    this.apiUrlInput = 'http://localhost:9082/api/recherche';
  }

  basculerVersNatif(): void {
    this.apiUrlInput = 'http://localhost:8082/api/recherche';
  }

  basculerVersGateway(): void {
    this.apiUrlInput = 'http://localhost:8080/api/recherche';
  }

  // --- TOASTS NOTIFICATIONS ---
  showToast(type: 'success' | 'error' | 'info', title: string, message: string): void {
    const id = Date.now();
    this.toasts.push({ id, type, title, message });
    setTimeout(() => {
      this.closeToast(id);
    }, 5000);
  }

  closeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }
}
