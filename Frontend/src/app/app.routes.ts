import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { BagagesComponent } from './components/bagages/bagages.component';
import { RechercheConsultationComponent } from './components/recherche-consultation/recherche-consultation.component';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'voyages', component: RechercheConsultationComponent },
  { path: 'recherche', redirectTo: 'voyages', pathMatch: 'full' },
  { path: 'bagages', component: BagagesComponent },
  { path: '**', redirectTo: '' }
];
