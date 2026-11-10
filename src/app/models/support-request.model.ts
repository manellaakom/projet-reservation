export enum SupportStatus {
  EN_ATTENTE = 'EN_ATTENTE',
  EN_COURS = 'EN_COURS',
  RESOLU = 'RESOLU',
  FERME = 'FERME'
}

export interface SupportRequest {
  id?: number;
  sujet: string;
  description: string;
  categorie: string;
  status: SupportStatus | string;
  dateCreation?: string;
  dateMiseAJour?: string;
  clientId?: number | null;
  agentId?: number | null;
  reservationId?: number | null;
}

export interface SupportRequestDto {
  sujet: string;
  description: string;
  categorie: string;
  status?: SupportStatus | string;
  clientId?: number | null;
  agentId?: number | null;
  reservationId?: number | null;
}

export interface SupportStats {
  total: number;
  enAttente: number;
  enCours: number;
  resolu: number;
  ferme: number;
}
