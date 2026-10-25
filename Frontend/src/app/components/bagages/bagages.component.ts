import { Component, OnInit, ChangeDetectorRef, ApplicationRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { finalize } from 'rxjs';
import { BagageService } from '../../services/bagage.service';
import { Bagage, BagageRequest, BagageStatistics } from '../../models/bagage.model';

interface ToastNotification {
  id: number;
  type: 'success' | 'error' | 'info';
  title: string;
  message: string;
}

export interface OperationLog {
  id: string;
  timestamp: Date;
  action: 'AJOUT' | 'MODIFICATION' | 'ASSOCIATION' | 'SUPPRESSION';
  bagageId: string;
  details: string;
  badgeClass: string;
}

function categoryWeightConsistencyValidator(control: AbstractControl): ValidationErrors | null {
  const type = control.get('type')?.value;
  const poids = control.get('poids')?.value;

  if (!type || poids === null || poids === undefined || poids === '') {
    return null;
  }

  const numWeight = Number(poids);
  if (isNaN(numWeight)) return null;

  if (numWeight <= 0) {
    return { categoryWeightMismatch: 'Le poids du bagage doit être strictement positif (> 0 kg).' };
  }
  if (numWeight > 30.0) {
    return { categoryWeightMismatch: 'Bagage rejeté : le poids dépasse la limite maximale autorisée de 30.0 kg.' };
  }

  if (type.includes('0–10') || type.toLowerCase().includes('cabine')) {
    if (numWeight > 10.0) {
      return { categoryWeightMismatch: `Incohérence : une valise cabine est limitée à 10.0 kg maximum (poids saisi : ${numWeight} kg).` };
    }
  } else if (type.includes('10–20')) {
    if (numWeight <= 10.0 || numWeight > 20.0) {
      return { categoryWeightMismatch: `Incohérence : le bagage moyen en soute doit peser entre 10.1 et 20.0 kg (poids saisi : ${numWeight} kg).` };
    }
  } else if (type.includes('20–30')) {
    if (numWeight <= 20.0 || numWeight > 30.0) {
      return { categoryWeightMismatch: `Incohérence : le grand bagage en soute doit peser entre 20.1 et 30.0 kg (poids saisi : ${numWeight} kg).` };
    }
  }

  return null;
}

@Component({
  selector: 'app-bagages',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './bagages.component.html',
  styleUrls: ['./bagages.component.scss']
})
export class BagagesComponent implements OnInit {
  bagages: Bagage[] = [];
  filteredBagages: Bagage[] = [];
  statistics: BagageStatistics | null = null;
  loading: boolean = false;
  error: string | null = null;

  // Filtres et Tri avancés
  searchTerm: string = '';
  selectedStatutFilter: string = 'ALL';
  selectedTypeFilter: string = 'ALL';
  sortColumn: 'poids' | 'prix' | 'reservationId' | 'id' | 'statut' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  // Historique des opérations (Session)
  operationsHistory: OperationLog[] = [];
  showHistoryModal: boolean = false;

  // Modal Ajout / Modification
  showFormModal: boolean = false;
  isEditMode: boolean = false;
  currentBagageId: string | null = null;
  bagageForm!: FormGroup;
  formError: string | null = null;
  submitting: boolean = false;

  // Modal Consultation
  showDetailModal: boolean = false;
  selectedBagage: Bagage | null = null;

  // Modal Association Réservation
  showAssociateModal: boolean = false;
  bagageToAssociate: Bagage | null = null;
  targetReservationId: string = '';
  associateError: string | null = null;
  associating: boolean = false;

  // Modal Suppression
  showDeleteModal: boolean = false;
  bagageToDelete: Bagage | null = null;

  // Modales Analytiques pour Cartes Statistiques
  showWeightDetailModal: boolean = false;
  showRevenueDetailModal: boolean = false;
  showAverageWeightDetailModal: boolean = false;

  // Toasts
  toasts: ToastNotification[] = [];
  private toastCounter: number = 0;

  // Types de bagages suggérés
  readonly baggageTypes: string[] = [
    'Valise cabine (0–10 kg)',
    'Bagage en soute moyen (10–20 kg)',
    'Grand bagage en soute (20–30 kg)',
    'Sac de voyage',
    'Matériel spécial / Équipement'
  ];

  constructor(
    private bagageService: BagageService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private appRef: ApplicationRef
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadAllData();
  }

  /**
   * Forçage de la détection de changements pour Angular Zoneless (sans Zone.js)
   * Garantit l'actualisation immédiate du DOM après les événements et requêtes HTTP.
   */
  triggerChangeDetection(): void {
    try {
      this.cdr.detectChanges();
    } catch {
      try {
        this.appRef.tick();
      } catch {
        this.cdr.markForCheck();
      }
    }
  }

  private initForm(): void {
    this.bagageForm = this.fb.group({
      reservationId: ['', [Validators.required]],
      type: ['Valise cabine (0–10 kg)', [Validators.required]],
      poids: [10.0, [Validators.required, Validators.min(0.1), Validators.max(30.0)]],
      statut: ['REGISTERED', [Validators.required]]
    }, { validators: [categoryWeightConsistencyValidator] });
  }

  onTypeChange(): void {
    const type = this.bagageForm.get('type')?.value;
    const currentWeight = this.bagageForm.get('poids')?.value;

    if (type?.includes('0–10') && (currentWeight > 10.0 || !currentWeight)) {
      this.bagageForm.patchValue({ poids: 10.0 });
    } else if (type?.includes('10–20') && (currentWeight <= 10.0 || currentWeight > 20.0 || !currentWeight)) {
      this.bagageForm.patchValue({ poids: 15.0 });
    } else if (type?.includes('20–30') && (currentWeight <= 20.0 || currentWeight > 30.0 || !currentWeight)) {
      this.bagageForm.patchValue({ poids: 25.0 });
    }
    this.triggerChangeDetection();
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.submitting = false;
    this.formError = null;
    this.triggerChangeDetection();
  }

  closeAssociateModal(): void {
    this.showAssociateModal = false;
    this.associating = false;
    this.associateError = null;
    this.triggerChangeDetection();
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedBagage = null;
    this.triggerChangeDetection();
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.bagageToDelete = null;
    this.triggerChangeDetection();
  }

  // --- Actions Modales Analytiques ---

  openWeightDetailModal(): void {
    this.showWeightDetailModal = true;
    this.triggerChangeDetection();
  }

  closeWeightDetailModal(): void {
    this.showWeightDetailModal = false;
    this.triggerChangeDetection();
  }

  openRevenueDetailModal(): void {
    this.showRevenueDetailModal = true;
    this.triggerChangeDetection();
  }

  closeRevenueDetailModal(): void {
    this.showRevenueDetailModal = false;
    this.triggerChangeDetection();
  }

  openAverageWeightDetailModal(): void {
    this.showAverageWeightDetailModal = true;
    this.triggerChangeDetection();
  }

  closeAverageWeightDetailModal(): void {
    this.showAverageWeightDetailModal = false;
    this.triggerChangeDetection();
  }

  // --- Agrégations Analytiques réelles pour les cartes statistiques ---

  get weightBreakdown() {
    const list = this.bagages;
    const totalW = Math.round(list.reduce((s, b) => s + (b.poids || 0), 0) * 10) / 10;
    const cabin = list.filter(b => (b.poids || 0) <= 10.0);
    const medium = list.filter(b => (b.poids || 0) > 10.0 && (b.poids || 0) <= 20.0);
    const large = list.filter(b => (b.poids || 0) > 20.0);

    const cabinW = Math.round(cabin.reduce((s, b) => s + (b.poids || 0), 0) * 10) / 10;
    const mediumW = Math.round(medium.reduce((s, b) => s + (b.poids || 0), 0) * 10) / 10;
    const largeW = Math.round(large.reduce((s, b) => s + (b.poids || 0), 0) * 10) / 10;

    return {
      totalWeight: totalW,
      count: list.length,
      cabin: { count: cabin.length, weight: cabinW, pct: totalW > 0 ? Math.round((cabinW / totalW) * 100) : 0 },
      medium: { count: medium.length, weight: mediumW, pct: totalW > 0 ? Math.round((mediumW / totalW) * 100) : 0 },
      large: { count: large.length, weight: largeW, pct: totalW > 0 ? Math.round((largeW / totalW) * 100) : 0 },
      items: list.map(b => ({
        id: b.id,
        reservationId: b.reservationId,
        type: b.type,
        poids: b.poids,
        statut: b.statut,
        pctOfMax: Math.round(((b.poids || 0) / 30.0) * 100)
      }))
    };
  }

  get revenueBreakdown() {
    const list = this.bagages;
    const totalRev = Math.round(list.reduce((s, b) => s + (b.prix || 0), 0) * 10) / 10;
    const tier30 = list.filter(b => b.prix === 30 || (b.prix == null && (b.poids || 0) <= 10));
    const tier50 = list.filter(b => b.prix === 50 || (b.prix == null && (b.poids || 0) > 10 && (b.poids || 0) <= 20));
    const tier70 = list.filter(b => b.prix === 70 || (b.prix == null && (b.poids || 0) > 20));

    const rev30 = tier30.reduce((s, b) => s + (b.prix || 30), 0);
    const rev50 = tier50.reduce((s, b) => s + (b.prix || 50), 0);
    const rev70 = tier70.reduce((s, b) => s + (b.prix || 70), 0);

    return {
      totalRevenue: totalRev,
      averagePerBagage: list.length > 0 ? Math.round((totalRev / list.length) * 10) / 10 : 0,
      tier30: { count: tier30.length, revenue: rev30, pct: totalRev > 0 ? Math.round((rev30 / totalRev) * 100) : 0 },
      tier50: { count: tier50.length, revenue: rev50, pct: totalRev > 0 ? Math.round((rev50 / totalRev) * 100) : 0 },
      tier70: { count: tier70.length, revenue: rev70, pct: totalRev > 0 ? Math.round((rev70 / totalRev) * 100) : 0 },
      items: list.map(b => ({
        id: b.id,
        reservationId: b.reservationId,
        type: b.type,
        poids: b.poids,
        prix: b.prix,
        tier: (b.poids || 0) <= 10 ? 'Palier 0–10 kg : 30 DT' : ((b.poids || 0) <= 20 ? 'Palier 10–20 kg : 50 DT' : 'Palier 20–30 kg : 70 DT')
      }))
    };
  }

  get averageWeightDetails() {
    const list = this.bagages;
    const totalW = Math.round(list.reduce((s, b) => s + (b.poids || 0), 0) * 10) / 10;
    const count = list.length;
    const avg = count > 0 ? Math.round((totalW / count) * 100) / 100 : 0;
    const weights = list.map(b => b.poids || 0);
    const minW = weights.length > 0 ? Math.min(...weights) : 0;
    const maxW = weights.length > 0 ? Math.max(...weights) : 0;

    return {
      totalWeight: totalW,
      count,
      average: avg,
      minWeight: minW,
      maxWeight: maxW,
      maxAllowed: 30.0,
      marginToMax: Math.round((30.0 - avg) * 100) / 100
    };
  }

  loadAllData(): void {
    this.loading = true;
    this.error = null;
    this.triggerChangeDetection();

    this.bagageService.getAllBagages().pipe(
      finalize(() => {
        this.loading = false;
        this.triggerChangeDetection();
      })
    ).subscribe({
      next: (data) => {
        this.bagages = data || [];
        this.applyFilter();
        this.computeFallbackStats();
        this.loadStatistics();
        this.triggerChangeDetection();
      },
      error: (err) => {
        console.error('Erreur Gateway bagages:', err);
        const errMsg = this.extractErrorMessage(err);
        this.error = `Communication Gateway interrompue (port 8080) : ${errMsg}`;
        this.showToast('error', 'Erreur de connexion', this.error);
        this.triggerChangeDetection();
      }
    });
  }

  loadStatistics(): void {
    this.bagageService.getStatistics().subscribe({
      next: (stats) => {
        if (stats) {
          this.statistics = stats;
          this.triggerChangeDetection();
        }
      },
      error: (err) => {
        console.warn('Statistiques backend directes non disponibles, calcul local:', err);
        this.computeFallbackStats(true);
        this.triggerChangeDetection();
      }
    });
  }

  get registeredCount(): number {
    return this.bagages.filter(b => {
      const s = (b.statut || '').toUpperCase();
      return s.includes('REGISTER') || s.includes('ENREGISTR') || s.includes('VALID') || s.includes('CONFIRM');
    }).length;
  }

  get pendingCount(): number {
    return this.bagages.filter(b => {
      const s = (b.statut || '').toUpperCase();
      return s.includes('PENDING') || s.includes('ATTENTE');
    }).length;
  }

  private computeFallbackStats(force: boolean = false): void {
    if (force || !this.statistics) {
      if (this.bagages.length === 0) {
        this.statistics = {
          totalBagages: 0,
          totalWeight: 0,
          totalRevenue: 0,
          averageWeight: 0,
          pendingBagages: 0,
          registeredBagages: 0
        };
        return;
      }
      const totalWeight = Math.round(this.bagages.reduce((sum, b) => sum + (b.poids || 0), 0) * 10) / 10;
      const totalRevenue = Math.round(this.bagages.reduce((sum, b) => sum + (b.prix || 0), 0) * 10) / 10;
      const pendingBagages = this.pendingCount;
      const registeredBagages = this.registeredCount;
      const averageWeight = Math.round((totalWeight / this.bagages.length) * 10) / 10;
      this.statistics = {
        totalBagages: this.bagages.length,
        totalWeight,
        totalRevenue,
        averageWeight,
        pendingBagages,
        registeredBagages
      };
    }
  }

  applyFilter(): void {
    let list = [...this.bagages];

    // 1. Filtre par statut
    if (this.selectedStatutFilter === 'REGISTER') {
      list = list.filter(b => {
        const s = (b.statut || '').toUpperCase();
        return s.includes('REGISTER') || s.includes('ENREGISTR') || s.includes('VALID') || s.includes('CONFIRM');
      });
    } else if (this.selectedStatutFilter === 'PENDING') {
      list = list.filter(b => {
        const s = (b.statut || '').toUpperCase();
        return s.includes('PENDING') || s.includes('ATTENTE');
      });
    }

    // 2. Filtre par type de bagage
    if (this.selectedTypeFilter !== 'ALL') {
      list = list.filter(b => {
        const t = (b.type || '').toLowerCase();
        if (this.selectedTypeFilter === 'CABIN') {
          return t.includes('0–10') || t.includes('cabine') || (b.poids || 0) <= 10.0;
        } else if (this.selectedTypeFilter === 'MEDIUM') {
          return t.includes('10–20') || ((b.poids || 0) > 10.0 && (b.poids || 0) <= 20.0);
        } else if (this.selectedTypeFilter === 'LARGE') {
          return t.includes('20–30') || (b.poids || 0) > 20.0;
        } else if (this.selectedTypeFilter === 'SPECIAL') {
          return t.includes('voyage') || t.includes('spécial') || t.includes('équipement');
        }
        return true;
      });
    }

    // 3. Recherche textuelle multi-critères (ID, réservation, type, statut)
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const term = this.searchTerm.trim().toLowerCase();
      list = list.filter(b =>
        (b.id && b.id.toLowerCase().includes(term)) ||
        (b.reservationId && b.reservationId.toLowerCase().includes(term)) ||
        (b.type && b.type.toLowerCase().includes(term)) ||
        (b.statut && b.statut.toLowerCase().includes(term))
      );
    }

    // 4. Tri dynamique
    if (this.sortColumn) {
      const col = this.sortColumn;
      const factor = this.sortDirection === 'asc' ? 1 : -1;

      list.sort((a, b) => {
        if (col === 'poids') {
          return ((a.poids || 0) - (b.poids || 0)) * factor;
        }
        if (col === 'prix') {
          return ((a.prix || 0) - (b.prix || 0)) * factor;
        }
        if (col === 'reservationId') {
          const valA = (a.reservationId || '').toLowerCase();
          const valB = (b.reservationId || '').toLowerCase();
          return valA.localeCompare(valB) * factor;
        }
        if (col === 'statut') {
          const valA = (a.statut || '').toLowerCase();
          const valB = (b.statut || '').toLowerCase();
          return valA.localeCompare(valB) * factor;
        }
        if (col === 'id') {
          return (a.id || '').localeCompare(b.id || '') * factor;
        }
        return 0;
      });
    }

    this.filteredBagages = list;
  }

  onSearchChange(): void {
    this.applyFilter();
  }

  onStatutFilterChange(statut: string): void {
    this.selectedStatutFilter = statut;
    this.applyFilter();
    this.triggerChangeDetection();
  }

  onTypeFilterChange(type: string): void {
    this.selectedTypeFilter = type;
    this.applyFilter();
    this.triggerChangeDetection();
  }

  setSort(column: 'poids' | 'prix' | 'reservationId' | 'id' | 'statut'): void {
    if (this.sortColumn === column) {
      if (this.sortDirection === 'asc') {
        this.sortDirection = 'desc';
      } else {
        this.sortColumn = null;
        this.sortDirection = 'asc';
      }
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applyFilter();
    this.triggerChangeDetection();
  }

  resetAllFilters(): void {
    this.searchTerm = '';
    this.selectedStatutFilter = 'ALL';
    this.selectedTypeFilter = 'ALL';
    this.sortColumn = null;
    this.sortDirection = 'asc';
    this.applyFilter();
    this.triggerChangeDetection();
  }

  /**
   * Export des bagages filtrés au format CSV compatible Microsoft Excel (UTF-8 avec BOM, séparateur point-virgule)
   */
  exportToCsv(): void {
    const list = this.filteredBagages;
    if (!list || list.length === 0) {
      this.showToast('info', 'Export CSV', 'Aucun bagage à exporter selon les filtres actifs.');
      return;
    }

    const headers = ['Identifiant (UUID)', 'N° Réservation', 'Type de Bagage', 'Poids Pesé (kg)', 'Statut', 'Tarif Backend (DT)'];
    const rows = list.map(b => [
      `"${b.id || ''}"`,
      `"${b.reservationId || 'Non rattaché'}"`,
      `"${(b.type || '').replace(/"/g, '""')}"`,
      `${b.poids ?? 0}`,
      `"${b.statut || ''}"`,
      `${b.prix ?? 0}`
    ]);

    const csvContent = '\uFEFF' + [headers.join(';'), ...rows.map(r => r.join(';'))].join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    const dateStr = new Date().toISOString().slice(0, 10);
    link.setAttribute('href', url);
    link.setAttribute('download', `skyvoyage_bagages_${dateStr}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);

    this.showToast('success', 'Export Excel réussi', `${list.length} bagage(s) exporté(s) au format CSV compatible Excel.`);
  }

  // --- Gestion de l'historique des opérations de session ---

  private logOperation(action: 'AJOUT' | 'MODIFICATION' | 'ASSOCIATION' | 'SUPPRESSION', bagageId: string, details: string): void {
    const badgeClasses: Record<string, string> = {
      'AJOUT': 'tag-success',
      'MODIFICATION': 'tag-primary',
      'ASSOCIATION': 'tag-info',
      'SUPPRESSION': 'tag-danger'
    };
    this.operationsHistory.unshift({
      id: 'op-' + Date.now() + '-' + Math.random().toString(36).slice(2, 6),
      timestamp: new Date(),
      action,
      bagageId,
      details,
      badgeClass: badgeClasses[action] || 'tag-neutral'
    });
  }

  openHistoryModal(): void {
    this.showHistoryModal = true;
    this.triggerChangeDetection();
  }

  closeHistoryModal(): void {
    this.showHistoryModal = false;
    this.triggerChangeDetection();
  }

  clearHistory(): void {
    this.operationsHistory = [];
    this.showToast('info', 'Historique réinitialisé', 'L\'historique des opérations de la session a été vidé.');
    this.triggerChangeDetection();
  }

  /**
   * Filtrage interactif au clic sur les cartes statistiques
   * Si 'ALL' : affiche tous les bagages et vide la recherche textuelle
   * Si 'REGISTER' ou 'PENDING' : filtre sur le statut correspondant (toggle si déjà sélectionné)
   */
  filterByCard(statut: string): void {
    if (statut === 'ALL') {
      this.selectedStatutFilter = 'ALL';
      this.searchTerm = '';
    } else {
      this.selectedStatutFilter = (this.selectedStatutFilter === statut) ? 'ALL' : statut;
    }
    this.applyFilter();
    this.triggerChangeDetection();
  }

  get estimatedPrice(): number | null {
    const poids = this.bagageForm.get('poids')?.value;
    return this.bagageService.calculateEstimatedPrice(poids);
  }

  // --- Actions CRUD ---

  openCreateModal(): void {
    this.isEditMode = false;
    this.currentBagageId = null;
    this.formError = null;
    this.submitting = false;
    this.bagageForm.reset({
      reservationId: '',
      type: 'Valise cabine (0–10 kg)',
      poids: 10.0,
      statut: 'REGISTERED'
    });
    this.showFormModal = true;
    this.triggerChangeDetection();
  }

  openEditModal(bagage: Bagage): void {
    this.isEditMode = true;
    this.currentBagageId = bagage.id;
    this.formError = null;
    this.submitting = false;
    this.bagageForm.patchValue({
      reservationId: bagage.reservationId || '',
      type: bagage.type || 'Valise cabine (0–10 kg)',
      poids: bagage.poids,
      statut: bagage.statut || 'REGISTERED'
    });
    this.showFormModal = true;
    this.triggerChangeDetection();
  }

  saveBagage(): void {
    // 1. Verrouillage strict anti-double soumission
    if (this.submitting) {
      return;
    }

    if (this.bagageForm.invalid) {
      this.bagageForm.markAllAsTouched();
      this.triggerChangeDetection();
      return;
    }

    // 2. Déclenchement de l'état de chargement immédiat
    this.formError = null;
    this.submitting = true;
    this.triggerChangeDetection();

    const formValue = this.bagageForm.value;
    const request: BagageRequest = {
      reservationId: formValue.reservationId?.trim(),
      type: formValue.type?.trim(),
      poids: Number(formValue.poids),
      statut: formValue.statut
    };

    if (this.isEditMode && this.currentBagageId) {
      this.bagageService.updateBagage(this.currentBagageId, request).pipe(
        finalize(() => {
          this.submitting = false;
          this.triggerChangeDetection();
        })
      ).subscribe({
        next: (updated) => {
          this.logOperation('MODIFICATION', updated.id, `Poids: ${updated.poids} kg, Tarif: ${updated.prix} DT, Statut: ${updated.statut}`);
          this.showToast('success', 'Bagage modifié', `Le bagage ${updated.id.substring(0, 8)}... a été mis à jour avec succès (${updated.prix} DT).`);
          this.closeFormModal();
          this.loadAllData();
          this.triggerChangeDetection();
        },
        error: (err) => {
          this.formError = this.extractErrorMessage(err);
          this.showToast('error', 'Erreur de modification', this.formError);
          this.triggerChangeDetection();
        }
      });
    } else {
      this.bagageService.createBagage(request).pipe(
        finalize(() => {
          this.submitting = false;
          this.triggerChangeDetection();
        })
      ).subscribe({
        next: (created) => {
          this.logOperation('AJOUT', created.id, `Type: ${created.type}, Poids: ${created.poids} kg, Tarif: ${created.prix} DT, Rés: ${created.reservationId || 'aucune'}`);
          this.showToast('success', 'Bagage enregistré', `Nouveau bagage créé (${created.prix} DT) et persisté dans Redis.`);
          this.closeFormModal();
          this.loadAllData();
          this.triggerChangeDetection();
        },
        error: (err) => {
          this.formError = this.extractErrorMessage(err);
          this.showToast('error', 'Erreur d\'ajout', this.formError);
          this.triggerChangeDetection();
        }
      });
    }
  }

  openDetailModal(bagage: Bagage): void {
    this.selectedBagage = bagage;
    this.showDetailModal = true;
    this.triggerChangeDetection();
  }

  openAssociateModal(bagage: Bagage): void {
    this.bagageToAssociate = bagage;
    this.targetReservationId = bagage.reservationId || '';
    this.associateError = null;
    this.associating = false;
    this.showAssociateModal = true;
    this.triggerChangeDetection();
  }

  openAssociateFromDetail(): void {
    if (!this.selectedBagage) return;
    const b = this.selectedBagage;
    this.closeDetailModal();
    this.openAssociateModal(b);
  }

  confirmAssociation(): void {
    if (!this.bagageToAssociate || !this.targetReservationId.trim() || this.associating) {
      return;
    }

    this.associateError = null;
    this.associating = true;
    this.triggerChangeDetection();

    this.bagageService.associateToReservation(this.bagageToAssociate.id, this.targetReservationId.trim()).pipe(
      finalize(() => {
        this.associating = false;
        this.triggerChangeDetection();
      })
    ).subscribe({
      next: (res) => {
        this.logOperation('ASSOCIATION', res.id, `Rattaché à la réservation : ${res.reservationId}`);
        this.showToast('success', 'Association réussie', `Bagage rattaché avec succès au dossier voyageur : ${res.reservationId}`);
        this.closeAssociateModal();
        this.loadAllData();
        this.triggerChangeDetection();
      },
      error: (err) => {
        this.associateError = this.extractErrorMessage(err);
        this.showToast('error', 'Erreur d\'association', this.associateError);
        this.triggerChangeDetection();
      }
    });
  }

  openDeleteModal(bagage: Bagage): void {
    this.bagageToDelete = bagage;
    this.showDeleteModal = true;
    this.triggerChangeDetection();
  }

  confirmDelete(): void {
    if (!this.bagageToDelete) return;

    const id = this.bagageToDelete.id;
    this.bagageService.deleteBagage(id).subscribe({
      next: () => {
        this.logOperation('SUPPRESSION', id, `Bagage retiré de la base de données Redis`);
        this.showToast('info', 'Bagage supprimé', `Le bagage a été supprimé de la base Redis.`);
        this.closeDeleteModal();
        this.loadAllData();
        this.triggerChangeDetection();
      },
      error: (err) => {
        const errMsg = this.extractErrorMessage(err);
        this.showToast('error', 'Erreur de suppression', errMsg);
        this.closeDeleteModal();
        this.triggerChangeDetection();
      }
    });
  }

  /**
   * Extrait un message d'erreur clair et détaillé depuis la réponse du Backend
   * Gère les exceptions métiers (BaggageWeightExceededException), Bean Validation (@Valid), et Gateway 500/timeout.
   */
  extractErrorMessage(err: any): string {
    if (!err) return 'Une erreur inconnue est survenue.';

    // Corps JSON brut stringifié
    if (typeof err.error === 'string') {
      try {
        const parsed = JSON.parse(err.error);
        if (parsed.error) return parsed.error;
        if (parsed.message) return parsed.message;
      } catch {
        if (err.error.length < 200) return err.error;
      }
    }

    // Exception globale du Backend : { "error": "Bagage refusé..." }
    if (err?.error?.error && typeof err.error.error === 'string') {
      return err.error.error;
    }

    // Message standard Spring Boot : { "message": "..." }
    if (err?.error?.message && typeof err.error.message === 'string') {
      return err.error.message;
    }

    // Violations @Valid Spring : { "poids": "Le poids ne peut pas dépasser 30 kg", "type": "..." }
    if (err?.error && typeof err.error === 'object') {
      const fieldErrors = Object.entries(err.error)
        .filter(([key]) => key !== 'timestamp' && key !== 'status' && key !== 'path')
        .map(([field, msg]) => `${field} : ${msg}`)
        .join(' • ');
      if (fieldErrors.length > 0) {
        return fieldErrors;
      }
    }

    if (err?.status === 0) {
      return 'Impossible de joindre l\'API Gateway sur http://localhost:8080 (Service hors ligne ou indisponible).';
    }
    if (err?.status === 404) {
      return 'Le bagage demandé est introuvable ou a déjà été supprimé.';
    }
    if (err?.status === 500) {
      return 'Erreur interne du microservice ou de l\'API Gateway (Vérifiez qu\'Eureka et Redis sont bien démarrés).';
    }

    return err?.statusText || err?.message || 'Erreur lors de la communication avec le serveur.';
  }

  // --- Helpers UI ---

  getStatutTagClass(statut: string | undefined): string {
    if (!statut) return 'tag-neutral';
    const s = statut.toUpperCase();
    if (s.includes('REGISTER') || s.includes('ENREGISTR') || s.includes('VALID') || s.includes('CONFIRM')) {
      return 'tag-success';
    }
    if (s.includes('PENDING') || s.includes('ATTENTE')) {
      return 'tag-warning';
    }
    return 'tag-neutral';
  }

  getWeightProgressPercent(poids: number): number {
    return Math.min(100, Math.round((poids / 30.0) * 100));
  }

  getWeightBadgeClass(poids: number): string {
    if (poids > 25) return 'high';
    if (poids > 15) return 'medium';
    return 'low';
  }

  showToast(type: 'success' | 'error' | 'info', title: string, message: string): void {
    const id = ++this.toastCounter;
    this.toasts.push({ id, type, title, message });
    this.triggerChangeDetection();
    setTimeout(() => {
      this.closeToast(id);
    }, 4500);
  }

  closeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.triggerChangeDetection();
  }
}
