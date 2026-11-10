import { Injectable } from '@angular/core';
import Keycloak, { KeycloakInstance } from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class KeycloakService {
  private readonly client: KeycloakInstance = new Keycloak({
    url: 'http://localhost:8180',
    realm: 'skyvoyage',
    clientId: 'skyvoyage-frontend'
  });

  private initialized = false;

  async initialize(): Promise<boolean> {
    if (this.initialized) {
      return this.authenticated;
    }

    const authenticated = await this.client.init({
      onLoad: 'login-required',
      checkLoginIframe: false,
      pkceMethod: 'S256'
    });
    this.initialized = true;
    return authenticated;
  }

  get authenticated(): boolean {
    return this.client.authenticated === true;
  }

  get username(): string {
    return this.client.tokenParsed?.['preferred_username']?.toString() ?? 'Voyageur';
  }

  async getToken(): Promise<string | undefined> {
    if (!this.authenticated) {
      return undefined;
    }
    await this.client.updateToken(30);
    return this.client.token;
  }

  logout(): void {
    void this.client.logout({ redirectUri: window.location.origin });
  }
}
