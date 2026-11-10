import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { SupportRequest, SupportRequestDto, SupportStats, SupportStatus } from '../models/support-request.model';

@Injectable({
  providedIn: 'root'
})
export class SupportRequestService {
  // Connexion au microservice Symfony (port 8000) et fallback API Gateway (port 8080)
  private readonly directUrl = 'http://localhost:8000/api/support-requests';
  private readonly gatewayUrl = 'http://localhost:8080/api/support-requests';

  private activeUrl = this.directUrl;

  constructor(private http: HttpClient) {}

  /**
   * Basculer l'URL (Gateway ou direct)
   */
  setUseDirectBackend(useDirect: boolean): void {
    this.activeUrl = useDirect ? this.directUrl : this.gatewayUrl;
  }

  getActiveUrl(): string {
    return this.activeUrl;
  }

  private getFallbackUrl(): string {
    return this.activeUrl === this.directUrl ? this.gatewayUrl : this.directUrl;
  }

  /**
   * Récupérer toutes les demandes avec filtres optionnels
   */
  getAll(filters?: { status?: string; search?: string; reservationId?: number; clientId?: number }): Observable<SupportRequest[]> {
    let params = new HttpParams();

    if (filters?.status && filters.status !== 'ALL') {
      params = params.set('status', filters.status);
    }
    if (filters?.search && filters.search.trim() !== '') {
      params = params.set('search', filters.search.trim());
    }
    if (filters?.reservationId) {
      params = params.set('reservationId', filters.reservationId.toString());
    }
    if (filters?.clientId) {
      params = params.set('clientId', filters.clientId.toString());
    }

    return this.http.get<SupportRequest[]>(this.activeUrl, { params }).pipe(
      catchError(err => {
        return this.http.get<SupportRequest[]>(this.getFallbackUrl(), { params }).pipe(
          catchError(() => throwError(() => err))
        );
      })
    );
  }

  /**
   * Récupérer les statistiques du support
   */
  getStats(): Observable<SupportStats> {
    return this.http.get<SupportStats>(`${this.activeUrl}/stats`).pipe(
      catchError(err => {
        return this.http.get<SupportStats>(`${this.getFallbackUrl()}/stats`).pipe(
          catchError(() => throwError(() => err))
        );
      })
    );
  }

  /**
   * Consulter une demande par son identifiant
   */
  getById(id: number): Observable<SupportRequest> {
    return this.http.get<SupportRequest>(`${this.activeUrl}/${id}`).pipe(
      catchError(err => {
        return this.http.get<SupportRequest>(`${this.getFallbackUrl()}/${id}`).pipe(
          catchError(() => throwError(() => err))
        );
      })
    );
  }

  /**
   * Créer une nouvelle demande de support
   */
  create(dto: SupportRequestDto): Observable<SupportRequest> {
    return this.http.post<SupportRequest>(this.activeUrl, dto).pipe(
      catchError(err => {
        return this.http.post<SupportRequest>(this.getFallbackUrl(), dto).pipe(
          catchError(() => throwError(() => err))
        );
      })
    );
  }

  /**
   * Mettre à jour une demande
   */
  update(id: number, dto: Partial<SupportRequestDto>): Observable<SupportRequest> {
    return this.http.put<SupportRequest>(`${this.activeUrl}/${id}`, dto).pipe(
      catchError(err => {
        return this.http.put<SupportRequest>(`${this.getFallbackUrl()}/${id}`, dto).pipe(
          catchError(() => throwError(() => err))
        );
      })
    );
  }

  /**
   * Mettre à jour le statut
   */
  updateStatus(id: number, status: SupportStatus | string): Observable<SupportRequest> {
    return this.update(id, { status });
  }

  /**
   * Supprimer une demande
   */
  delete(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.activeUrl}/${id}`).pipe(
      catchError(err => {
        return this.http.delete<{ message: string }>(`${this.getFallbackUrl()}/${id}`).pipe(
          catchError(() => throwError(() => err))
        );
      })
    );
  }
}
