export interface Bagage {
  id: string;
  reservationId?: string;
  type: string;
  poids: number;
  statut: string;
  prix?: number;
}

export interface BagageRequest {
  reservationId?: string;
  type: string;
  poids: number;
  statut: string;
  prix?: number;
}

export interface BagageStatistics {
  totalBagages: number;
  totalWeight: number;
  totalRevenue: number;
  averageWeight: number;
  pendingBagages: number;
  registeredBagages: number;
}
