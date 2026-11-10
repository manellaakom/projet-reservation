import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { BagagesComponent } from './components/bagages/bagages.component';
import { RechercheConsultationComponent } from './components/recherche-consultation/recherche-consultation.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'voyages', component: RechercheConsultationComponent },
  { path: 'recherche', redirectTo: 'voyages', pathMatch: 'full' },
  { path: 'bagages', component: BagagesComponent },
  { path: '**', redirectTo: '' }
];
