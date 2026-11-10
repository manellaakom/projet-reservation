import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard protégeant les routes intérieures de l'application.
 * Si l'utilisateur n'est pas connecté via Keycloak/JWT,
 * il est automatiquement redirigé vers la page de connexion extérieure (/login).
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // Redirection vers la page externe de connexion
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url }
  });
  return false;
};
