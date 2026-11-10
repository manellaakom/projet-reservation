import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface KeycloakTokenResponse {
  access_token: string;
  expires_in: number;
  refresh_expires_in: number;
  refresh_token: string;
  token_type: string;
  id_token?: string;
  scope: string;
}

export interface UserProfile {
  username: string;
  roles: string[];
  isAdmin: boolean;
  isUser: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenEndpoint = 'http://localhost:8180/realms/skyvoyage/protocol/openid-connect/token';
  private readonly clientId = 'skyvoyage-client';
  private readonly TOKEN_STORAGE_KEY = 'skyvoyage_session_token';

  private currentTokenSubject = new BehaviorSubject<string | null>(this.getStoredToken());
  public currentToken$ = this.currentTokenSubject.asObservable();

  private currentUserSubject = new BehaviorSubject<UserProfile | null>(this.extractUserProfile(this.getStoredToken()));
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Authentification réelle auprès de Keycloak (flux Direct Access Grants)
   * Récupère un JWT signé par Keycloak sans identifiants ni tokens codés en dur.
   */
  login(username: string, password: string): Observable<KeycloakTokenResponse> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', this.clientId);
    body.set('username', username);
    body.set('password', password);

    return this.http.post<KeycloakTokenResponse>(this.tokenEndpoint, body.toString(), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    }).pipe(
      tap(response => {
        if (response && response.access_token) {
          this.setSession(response.access_token);
        }
      })
    );
  }

  logout(): void {
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.removeItem(this.TOKEN_STORAGE_KEY);
    }
    this.currentTokenSubject.next(null);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return this.currentTokenSubject.value;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return !!user && user.isAdmin;
  }

  getCurrentUser(): UserProfile | null {
    return this.currentUserSubject.value;
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return true;
      const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(atob(payloadBase64));
      if (!payload.exp) return false;
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }

  private setSession(token: string): void {
    if (typeof sessionStorage !== 'undefined') {
      sessionStorage.setItem(this.TOKEN_STORAGE_KEY, token);
    }
    this.currentTokenSubject.next(token);
    this.currentUserSubject.next(this.extractUserProfile(token));
  }

  private getStoredToken(): string | null {
    if (typeof sessionStorage !== 'undefined') {
      return sessionStorage.getItem(this.TOKEN_STORAGE_KEY);
    }
    return null;
  }

  /**
   * Décode le payload du JWT pour extraire les rôles Keycloak (realm_access.roles)
   * sans exposer le token ni nécessiter de secret client.
   */
  private extractUserProfile(token: string | null): UserProfile | null {
    if (!token) return null;
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const payloadJson = decodeURIComponent(
        atob(payloadBase64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const parsed = JSON.parse(payloadJson);

      const username = parsed.preferred_username || parsed.sub || 'Utilisateur';
      const realmRoles: string[] = parsed.realm_access?.roles || [];
      const isAdmin = realmRoles.includes('admin') || realmRoles.includes('ADMIN');
      const isUser = realmRoles.includes('user') || realmRoles.includes('USER');

      return {
        username,
        roles: realmRoles,
        isAdmin,
        isUser
      };
    } catch {
      return null;
    }
  }
}
