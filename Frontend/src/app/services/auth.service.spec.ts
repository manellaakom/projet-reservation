import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  // Base64 helper for tests: payload with preferred_username and realm_access roles
  const mockPayloadAdmin = btoa(JSON.stringify({
    preferred_username: 'admin',
    realm_access: { roles: ['admin', 'user'] }
  }));
  const mockTokenAdmin = `header.${mockPayloadAdmin}.signature`;

  const mockPayloadUser = btoa(JSON.stringify({
    preferred_username: 'voyageur',
    realm_access: { roles: ['user'] }
  }));
  const mockTokenUser = `header.${mockPayloadUser}.signature`;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('devrait être créé avec un état initial non authentifié', () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.getToken()).toBeNull();
    expect(service.isAdmin()).toBe(false);
  });

  it('devrait appeler Keycloak avec les bons paramètres lors du login', () => {
    service.login('admin', 'admin').subscribe(res => {
      expect(res.access_token).toBe(mockTokenAdmin);
      expect(service.isAuthenticated()).toBe(true);
      expect(service.isAdmin()).toBe(true);
      expect(service.getCurrentUser()?.username).toBe('admin');
    });

    const req = httpMock.expectOne('http://localhost:8180/realms/skyvoyage/protocol/openid-connect/token');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toContain('grant_type=password');
    expect(req.request.body).toContain('client_id=skyvoyage-client');
    expect(req.request.body).toContain('username=admin');

    req.flush({
      access_token: mockTokenAdmin,
      token_type: 'Bearer',
      expires_in: 300,
      refresh_expires_in: 1800,
      refresh_token: 'refresh',
      scope: 'openid'
    });
  });

  it('devrait identifier correctement un rôle user (voyageur)', () => {
    service.login('voyageur', 'user').subscribe(() => {
      expect(service.isAuthenticated()).toBe(true);
      expect(service.isAdmin()).toBe(false);
      expect(service.getCurrentUser()?.isUser).toBe(true);
      expect(service.getCurrentUser()?.username).toBe('voyageur');
    });

    const req = httpMock.expectOne('http://localhost:8180/realms/skyvoyage/protocol/openid-connect/token');
    req.flush({
      access_token: mockTokenUser,
      token_type: 'Bearer',
      expires_in: 300,
      refresh_expires_in: 1800,
      refresh_token: 'refresh',
      scope: 'openid'
    });
  });

  it('devrait réinitialiser la session lors du logout', () => {
    service.login('admin', 'admin').subscribe();
    const req = httpMock.expectOne('http://localhost:8180/realms/skyvoyage/protocol/openid-connect/token');
    req.flush({ access_token: mockTokenAdmin });

    expect(service.isAuthenticated()).toBe(true);
    service.logout();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.getToken()).toBeNull();
    expect(service.getCurrentUser()).toBeNull();
  });
});
