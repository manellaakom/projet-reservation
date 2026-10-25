import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bagage, BagageRequest, BagageStatistics } from '../models/bagage.model';

@Injectable({
  providedIn: 'root'
})
export class BagageService {
  // Connexion directe via l'API Gateway sur le port 8080
  private readonly apiUrl = 'http://localhost:8080/api/bagages';

  constructor(private http: HttpClient) {}

  getAllBagages(): Observable<Bagage[]> {
    return this.http.get<Bagage[]>(this.apiUrl);
  }

  getBagageById(id: string): Observable<Bagage> {
    return this.http.get<Bagage>(`${this.apiUrl}/${id}`);
  }

  createBagage(request: BagageRequest): Observable<Bagage> {
    return this.http.post<Bagage>(this.apiUrl, request);
  }

  updateBagage(id: string, request: BagageRequest): Observable<Bagage> {
    return this.http.put<Bagage>(`${this.apiUrl}/${id}`, request);
  }

  deleteBagage(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  associateToReservation(id: string, reservationId: string): Observable<Bagage> {
    return this.http.patch<Bagage>(`${this.apiUrl}/${id}/reservation/${encodeURIComponent(reservationId)}`, {});
  }

  getStatistics(): Observable<BagageStatistics> {
    return this.http.get<BagageStatistics>(`${this.apiUrl}/statistics`);
  }

  /**
   * Calcul estimé du prix selon les règles métier du Backend :
   * 0–10 kg -> 30 DT
   * >10–20 kg -> 50 DT
   * >20–30 kg -> 70 DT
   * >30 kg -> Non autorisé
   */
  calculateEstimatedPrice(poids: number | null | undefined): number | null {
    if (poids === null || poids === undefined || poids <= 0 || poids > 30) {
      return null;
    }
    if (poids <= 10) return 30.0;
    if (poids <= 20) return 50.0;
    return 70.0;
  }
}
