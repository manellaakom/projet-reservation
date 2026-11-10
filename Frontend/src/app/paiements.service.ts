import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Paiement {
  id: string;
  montant: number;
  datePaiement: string;
  statut: string;
  reference: string;
  paiementEnLigne: boolean;
}

export interface PaiementRequest {
  montant: number;
  datePaiement: string;
  statut: string;
  reference: string;
}

export interface PaymeeCheckoutRequest {
  montant: number;
  reference: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export interface PaymeeCheckoutResponse {
  paiementId: string;
  paymentUrl: string;
}

@Injectable({ providedIn: 'root' })
export class PaiementsService {
  private readonly apiUrl = '/api/paiements';

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(this.apiUrl);
  }

  create(request: PaiementRequest): Observable<Paiement> {
    return this.http.post<Paiement>(this.apiUrl, request);
  }

  createPaymeeCheckout(request: PaymeeCheckoutRequest): Observable<PaymeeCheckoutResponse> {
    return this.http.post<PaymeeCheckoutResponse>(`${this.apiUrl}/paymee/checkout`, request);
  }

  verifyPaymeePayment(id: string): Observable<Paiement> {
    return this.http.get<Paiement>(`${this.apiUrl}/${encodeURIComponent(id)}/verify`);
  }

  update(id: string, request: PaiementRequest): Observable<Paiement> {
    return this.http.put<Paiement>(`${this.apiUrl}/${encodeURIComponent(id)}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${encodeURIComponent(id)}`);
  }
}
