import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authService: AuthService;

  const mockToken = 'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxMjMifQ.sig';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpMock.verify();
    authService.logout();
  });

  it('devrait ajouter l en-tete Authorization Bearer si l utilisateur a un token et que l URL cible le port 8080', () => {
    // Simuler un token dans authService
    (authService as any).setSession(mockToken);

    httpClient.get('http://localhost:8080/api/bagages').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/bagages');
    expect(req.request.headers.has('Authorization')).toBe(true);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush([]);
  });

  it('ne devrait PAS ajouter d en-tete Authorization si aucun token n est present', () => {
    authService.logout();

    httpClient.get('http://localhost:8080/api/bagages').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/bagages');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush([]);
  });
});
