import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ComparaisonResponse,
  CritereRecherche,
  OffreBillet,
  SuggestionsResponse,
  VoyageOffre
} from '../models/recherche.model';

@Injectable({
  providedIn: 'root'
})
export class RechercheConsultationService {
  private readonly apiUrl = 'http://localhost:8082/api/recherche';

  constructor(private http: HttpClient) {}

  rechercherVoyages(criteres: CritereRecherche): Observable<VoyageOffre[]> {
    return this.http.post<VoyageOffre[]>(`${this.apiUrl}/voyages/search`, criteres);
  }

  rechercherBillets(params: {
    classe?: string;
    prixMax?: number;
    remboursable?: boolean;
    modifiable?: boolean;
    bagageSouteMinKg?: number;
    sortBy?: string;
    sortDir?: string;
  }): Observable<OffreBillet[]> {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        httpParams = httpParams.set(key, String(val));
      }
    });
    return this.http.get<OffreBillet[]>(`${this.apiUrl}/billets`, { params: httpParams });
  }

  consulterVoyageParId(id: number): Observable<VoyageOffre> {
    return this.http.get<VoyageOffre>(`${this.apiUrl}/voyages/${id}`);
  }

  comparerOffres(ids: number[]): Observable<ComparaisonResponse> {
    return this.http.post<ComparaisonResponse>(`${this.apiUrl}/comparer`, ids);
  }

  obtenirSuggestionsGlobales(villeDestination?: string, budgetMax?: number): Observable<SuggestionsResponse> {
    let params = new HttpParams();
    if (villeDestination) params = params.set('villeDestination', villeDestination);
    if (budgetMax) params = params.set('budgetMax', String(budgetMax));
    return this.http.get<SuggestionsResponse>(`${this.apiUrl}/suggestions`, { params });
  }

  obtenirSuggestionsPourOffre(voyageId: number): Observable<SuggestionsResponse> {
    return this.http.get<SuggestionsResponse>(`${this.apiUrl}/voyages/${voyageId}/suggestions`);
  }
}
