import { Component, OnInit } from '@angular/core';
import {
  ComparaisonResponse,
  CritereRecherche,
  OffreBillet,
  SuggestionsResponse,
  VoyageOffre
} from '../../models/recherche.model';
import { RechercheConsultationService } from '../../services/recherche-consultation.service';

@Component({
  selector: 'app-recherche-consultation',
  templateUrl: './recherche-consultation.component.html',
  styleUrls: ['./recherche-consultation.component.css']
})
export class RechercheConsultationComponent implements OnInit {
  voyages: VoyageOffre[] = [];
  billets: OffreBillet[] = [];
  offreConsultee: VoyageOffre | null = null;
  suggestionsOffre: SuggestionsResponse | null = null;
  suggestionsGlobales: SuggestionsResponse | null = null;
  comparaison: ComparaisonResponse | null = null;
  idsSelectionnesComparaison: number[] = [];

  criteres: CritereRecherche = {
    sortBy: 'prix',
    sortDir: 'asc',
    prixMax: 1800
  };

  constructor(private rechercheService: RechercheConsultationService) {}

  ngOnInit(): void {
    this.lancerRecherche();
    this.chargerSuggestionsGlobales();
  }

  lancerRecherche(): void {
    this.rechercheService.rechercherVoyages(this.criteres).subscribe(res => {
      this.voyages = res;
    });
  }

  consulterOffre(id: number): void {
    this.rechercheService.consulterVoyageParId(id).subscribe(offre => {
      this.offreConsultee = offre;
      this.rechercheService.obtenirSuggestionsPourOffre(id).subscribe(sugg => {
        this.suggestionsOffre = sugg;
      });
    });
  }

  basculerSelectionComparaison(id: number): void {
    const index = this.idsSelectionnesComparaison.indexOf(id);
    if (index >= 0) {
      this.idsSelectionnesComparaison.splice(index, 1);
    } else if (this.idsSelectionnesComparaison.length < 4) {
      this.idsSelectionnesComparaison.push(id);
    }
    if (this.idsSelectionnesComparaison.length >= 2) {
      this.rechercheService.comparerOffres(this.idsSelectionnesComparaison).subscribe(comp => {
        this.comparaison = comp;
      });
    } else {
      this.comparaison = null;
    }
  }

  chargerSuggestionsGlobales(destination?: string): void {
    this.rechercheService.obtenirSuggestionsGlobales(destination, this.criteres.prixMax).subscribe(res => {
      this.suggestionsGlobales = res;
    });
  }
}
