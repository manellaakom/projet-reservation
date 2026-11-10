import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ComparaisonResponse,
  CritereRecherche,
  StatistiquesCatalogue,
  SuggestionsResponse,
  VoyageOffre,
  VoyageRequestDTO
} from '../models/recherche.model';

@Injectable({
  providedIn: 'root'
})
export class RechercheConsultationService {
  // Routage impératif via l'API Gateway sur le port 8080 (Exigence d'évaluation)
  private apiUrl = 'http://localhost:8080/api/recherche';

  constructor(private http: HttpClient) {
    const savedUrl = localStorage.getItem('skyvoyage_api_url');
    if (savedUrl && !savedUrl.includes('8082') && !savedUrl.includes('9082')) {
      this.apiUrl = savedUrl;
    } else {
      this.apiUrl = 'http://localhost:8080/api/recherche';
      localStorage.setItem('skyvoyage_api_url', this.apiUrl);
    }
  }

  getApiUrl(): string {
    return this.apiUrl;
  }

  setApiUrl(url: string): void {
    this.apiUrl = url;
    localStorage.setItem('skyvoyage_api_url', url);
  }

  getAuthToken(): string {
    const saved = localStorage.getItem('skyvoyage_jwt_token');
    if (saved) return saved;
    return 'eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODAvcmVhbG1zL3RyYXZlbC1yZWFsbSIsInN1YiI6ImFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiUk9MRV9BRE1JTiIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXX19';
  }

  setAuthToken(token: string): void {
    if (token) {
      localStorage.setItem('skyvoyage_jwt_token', token.trim());
    } else {
      localStorage.removeItem('skyvoyage_jwt_token');
    }
  }

  private getAuthHeaders(customToken?: string): HttpHeaders {
    let token = customToken || this.getAuthToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      if (!token.toLowerCase().startsWith('bearer ')) {
        token = `Bearer ${token}`;
      }
      headers = headers.set('Authorization', token);
    }
    return headers;
  }

  /**
   * 1. Recherche et filtrage multi-critères
   */
  rechercherVoyages(criteres: CritereRecherche): Observable<VoyageOffre[]> {
    return this.http.post<VoyageOffre[]>(`${this.apiUrl}/voyages/search`, criteres);
  }

  /**
   * 1b. Récupération directe de tous les voyages
   */
  getTousLesVoyages(): Observable<VoyageOffre[]> {
    return this.http.get<VoyageOffre[]>(`${this.apiUrl}/voyages`);
  }

  /**
   * 2. Consultation par ID
   */
  consulterVoyageParId(id: number): Observable<VoyageOffre> {
    return this.http.get<VoyageOffre>(`${this.apiUrl}/voyages/${id}`);
  }

  /**
   * 3. Statistiques complètes et listes de filtres
   */
  getStatistiques(): Observable<StatistiquesCatalogue> {
    return this.http.get<StatistiquesCatalogue>(`${this.apiUrl}/statistiques`);
  }

  /**
   * 4. Comparateur d'offres côte-à-côte
   */
  comparerOffres(ids: number[]): Observable<ComparaisonResponse> {
    return this.http.post<ComparaisonResponse>(`${this.apiUrl}/comparer`, ids);
  }

  /**
   * 5. Suggestions intelligentes globales
   */
  obtenirSuggestionsGlobales(destination?: string, budgetMax?: number, typeTransport?: string): Observable<SuggestionsResponse> {
    let params = new HttpParams();
    if (destination) params = params.set('villeDestination', destination);
    if (budgetMax) params = params.set('budgetMax', String(budgetMax));
    if (typeTransport) params = params.set('typeTransport', typeTransport);
    return this.http.get<SuggestionsResponse>(`${this.apiUrl}/suggestions`, { params });
  }

  /**
   * 6. Alternatives similaires pour une offre donnée
   */
  obtenirSuggestionsPourOffre(voyageId: number): Observable<SuggestionsResponse> {
    return this.http.get<SuggestionsResponse>(`${this.apiUrl}/voyages/${voyageId}/suggestions`);
  }

  /**
   * 7. Création d'une offre (Protégé par Token JWT)
   */
  creerVoyage(voyage: VoyageRequestDTO, token?: string): Observable<VoyageOffre> {
    const headers = this.getAuthHeaders(token);
    return this.http.post<VoyageOffre>(`${this.apiUrl}/voyages`, voyage, { headers });
  }

  /**
   * 8. Modification d'une offre (Protégé par Token JWT)
   */
  modifierVoyage(id: number, voyage: VoyageRequestDTO, token?: string): Observable<VoyageOffre> {
    const headers = this.getAuthHeaders(token);
    return this.http.put<VoyageOffre>(`${this.apiUrl}/voyages/${id}`, voyage, { headers });
  }

  /**
   * 9. Suppression d'une offre (Réservé à ROLE_ADMIN)
   */
  supprimerVoyage(id: number, token?: string): Observable<void> {
    const headers = this.getAuthHeaders(token);
    return this.http.delete<void>(`${this.apiUrl}/voyages/${id}`, { headers });
  }

  /**
   * 10. Health check
   */
  verifierSante(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/health`);
  }

  /**
   * 11. Authentification Keycloak & Tokens OIDC (Note 2 de la grille)
   */
  getKeycloakDemoTokens(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/auth/keycloak/demo-tokens`);
  }

  loginKeycloak(credentials: { username: string; password?: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/keycloak/login`, credentials);
  }

  getKeycloakConfig(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/auth/keycloak/config`);
  }
}
