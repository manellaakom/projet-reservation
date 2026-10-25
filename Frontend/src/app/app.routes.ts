import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { BagagesComponent } from './components/bagages/bagages.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'bagages', component: BagagesComponent },
  { path: '**', redirectTo: '' }
];
