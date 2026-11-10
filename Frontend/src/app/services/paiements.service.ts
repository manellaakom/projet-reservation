import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

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
  private readonly gatewayUrl = 'http://localhost:8080/api/paiements';
  private readonly directUrl = 'http://localhost:8084/api/paiements';

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(this.gatewayUrl).pipe(
      catchError(() => this.http.get<Paiement[]>(this.directUrl))
    );
  }

  create(request: PaiementRequest): Observable<Paiement> {
    return this.http.post<Paiement>(this.gatewayUrl, request).pipe(
      catchError(() => this.http.post<Paiement>(this.directUrl, request))
    );
  }

  createPaymeeCheckout(request: PaymeeCheckoutRequest): Observable<PaymeeCheckoutResponse> {
    return this.http.post<PaymeeCheckoutResponse>(`${this.gatewayUrl}/paymee/checkout`, request).pipe(
      catchError(() => this.http.post<PaymeeCheckoutResponse>(`${this.directUrl}/paymee/checkout`, request))
    );
  }

  verifyPaymeePayment(id: string): Observable<Paiement> {
    return this.http.get<Paiement>(`${this.gatewayUrl}/${encodeURIComponent(id)}/verify`).pipe(
      catchError(() => this.http.get<Paiement>(`${this.directUrl}/${encodeURIComponent(id)}/verify`))
    );
  }

  update(id: string, request: PaiementRequest): Observable<Paiement> {
    return this.http.put<Paiement>(`${this.gatewayUrl}/${encodeURIComponent(id)}`, request).pipe(
      catchError(() => this.http.put<Paiement>(`${this.directUrl}/${encodeURIComponent(id)}`, request))
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/${encodeURIComponent(id)}`).pipe(
      catchError(() => this.http.delete<void>(`${this.directUrl}/${encodeURIComponent(id)}`))
    );
  }
}
