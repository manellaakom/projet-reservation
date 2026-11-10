import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { BagagesComponent } from './components/bagages/bagages.component';
import { RechercheConsultationComponent } from './components/recherche-consultation/recherche-consultation.component';
import { SupportClientComponent } from './components/support-client/support-client.component';
import { PaiementsComponent } from './components/paiements/paiements.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Page de connexion extérieure
  { path: 'login', component: LoginComponent },

  // Routes intérieures protégées par l'authentification
  { path: '', component: HomeComponent, canActivate: [authGuard], pathMatch: 'full' },
  { path: 'voyages', component: RechercheConsultationComponent, canActivate: [authGuard] },
  { path: 'recherche', redirectTo: 'voyages', pathMatch: 'full' },
  { path: 'bagages', component: BagagesComponent, canActivate: [authGuard] },
  { path: 'paiements', component: PaiementsComponent, canActivate: [authGuard] },
  { path: 'support', component: SupportClientComponent, canActivate: [authGuard] },

  // Redirection globale vers l'accueil (qui redirigera vers /login si non authentifié)
  { path: '**', redirectTo: '' }
];
