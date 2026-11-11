import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Intercepteur HTTP Angular :
 * 1. Attache automatiquement l'en-tête "Authorization: Bearer <token>" à toutes les requêtes vers la Gateway (port 8080).
 * 2. Gère proprement les erreurs 401 (non authentifié) et 403 (droits insuffisants) sans faire crasher l'application.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  let modifiedReq = req;

  // Injection du Bearer token pour les requêtes vers l'API Gateway ou microservices internes
  if (token && (req.url.includes(':8080') || req.url.includes('/api/'))) {
    modifiedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(modifiedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Déconnexion automatique si la Gateway ou le microservice rejette le token (401)
      if (error.status === 401) {
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};
