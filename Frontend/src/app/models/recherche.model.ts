export type TypeTransport = 'VOL' | 'TRAIN' | 'BUS' | 'BATEAU' | 'SEJOUR_PACK';
export type CategorieOffre = 'ECONOMIQUE' | 'CONFORT' | 'PREMIUM' | 'LUXE' | 'PROMO';
export type ClasseBillet = 'ECONOMIQUE' | 'PREMIUM_ECO' | 'BUSINESS' | 'PREMIERE';
export type StatutOffre = 'DISPONIBLE' | 'DERNIERES_PLACES' | 'COMPLET' | 'EXPIRE';

export interface OffreBillet {
  id?: number;
  codeOffreBillet: string;
  classe: ClasseBillet;
  prix: number;
  placesRestantes: number;
  bagageCabineKg: number;
  bagageSouteKg: number;
  modifiable: boolean;
  remboursable: boolean;
  choixSiegeGratuit: boolean;
  accesSalonVip: boolean;
  conditionsTarifaires?: string;
  voyageId?: number;
  codeVoyage?: string;
  titreVoyage?: string;
  villeDepart?: string;
  villeDestination?: string;
  compagnie?: string;
  typeTransport?: TypeTransport;
}

export interface VoyageOffre {
  id: number;
  codeVoyage: string;
  titre: string;
  description: string;
  villeDepart: string;
  villeDestination: string;
  paysDestination: string;
  compagnie: string;
  typeTransport: TypeTransport;
  categorie: CategorieOffre;
  dateDepart: string;
  dateArrivee: string;
  dureeMinutes: number;
  dureeFormatee: string;
  escales: number;
  prixBase: number;
  prixPromo?: number;
  prixEffectif: number;
  pourcentageReduction: number;
  enPromotion: boolean;
  placesDisponibles: number;
  noteMoyenne: number;
  nombreAvis: number;
  nombreConsultations: number;
  bagageCabineInclus: boolean;
  bagageSouteInclus: boolean;
  annulationGratuite: boolean;
  repasInclus: boolean;
  wifiDisponible: boolean;
  imageUrl?: string;
  statut: StatutOffre;
  scoreQualitePrix: number;
  badgesRecommandation: string[];
  avantagesCles: string[];
  billets: OffreBillet[];
}

export interface CritereRecherche {
  motCle?: string;
  villeDepart?: string;
  villeDestination?: string;
  paysDestination?: string;
  compagnie?: string;
  typeTransport?: TypeTransport;
  categorie?: CategorieOffre;
  classeBillet?: ClasseBillet;
  dateDepartMin?: string;
  dateDepartMax?: string;
  prixMin?: number;
  prixMax?: number;
  dureeMaxMinutes?: number;
  volDirectUniquement?: boolean;
  enPromotionUniquement?: boolean;
  bagageSouteInclus?: boolean;
  annulationGratuite?: boolean;
  placesMin?: number;
  noteMin?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface ComparaisonResponse {
  nombreOffresComparees: number;
  offreMoinsChereId: number;
  offreMoinsChereTitre: string;
  meilleurPrix: number;
  offrePlusRapideId: number;
  offrePlusRapideTitre: string;
  dureeMinimaleMinutes: number;
  offreMieuxNoteeId: number;
  offreMieuxNoteeTitre: string;
  meilleureNote: number;
  offreMeilleurRapportQualitePrixId: number;
  offreMeilleurRapportQualitePrixTitre: string;
  meilleurScoreQualitePrix: number;
  ecartPrixMax: number;
  ecartDureeMinutes: number;
  syntheseRecommandation: string;
  offresComparees: VoyageOffre[];
}

export interface SuggestionsResponse {
  contexteSuggestion: string;
  offresTendances: VoyageOffre[];
  meilleuresPromotions: VoyageOffre[];
  petitsBudgets: VoyageOffre[];
  offresSimilaires: VoyageOffre[];
  alternativesMoinsCheres: VoyageOffre[];
  meilleurRapportQualitePrix: VoyageOffre[];
}
