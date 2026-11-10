import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { BagagesComponent } from './bagages.component';
import { BagageService } from '../../services/bagage.service';
import { Bagage, BagageStatistics } from '../../models/bagage.model';

describe('BagagesComponent', () => {
  let component: BagagesComponent;
  let fixture: ComponentFixture<BagagesComponent>;
  let bagageService: BagageService;

  const mockBagages: Bagage[] = [
    { id: 'b1', reservationId: 'RES-001', type: 'CABINE', poids: 9.0, statut: 'REGISTERED', prix: 30.0 },
    { id: 'b2', reservationId: 'RES-002', type: 'SOUTE', poids: 18.0, statut: 'PENDING', prix: 50.0 }
  ];

  const mockStats: BagageStatistics = {
    totalBagages: 2,
    totalWeight: 27.0,
    totalRevenue: 80.0,
    averageWeight: 13.5,
    pendingBagages: 1,
    registeredBagages: 1
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BagagesComponent, HttpClientTestingModule],
      providers: [BagageService]
    }).compileComponents();

    fixture = TestBed.createComponent(BagagesComponent);
    component = fixture.componentInstance;
    bagageService = TestBed.inject(BagageService);
  });

  it('should create the component and initialize the form', () => {
    expect(component).toBeTruthy();
    expect(component.bagageForm).toBeDefined();
    expect(component.bagageForm.get('reservationId')).toBeDefined();
    expect(component.bagageForm.get('poids')).toBeDefined();
  });

  it('should load all bagages and statistics on init', () => {
    vi.spyOn(bagageService, 'getAllBagages').mockReturnValue(of(mockBagages));
    vi.spyOn(bagageService, 'getStatistics').mockReturnValue(of(mockStats));

    component.ngOnInit();

    expect(bagageService.getAllBagages).toHaveBeenCalled();
    expect(component.bagages.length).toBe(2);
    expect(component.filteredBagages.length).toBe(2);
    expect(component.statistics?.totalRevenue).toBe(80.0);
    expect(component.loading).toBe(false);
  });

  it('should stop spinner and set error banner if backend/gateway fails', () => {
    vi.spyOn(bagageService, 'getAllBagages').mockReturnValue(throwError(() => ({ status: 500, message: 'Gateway Timeout' })));
    vi.spyOn(bagageService, 'getStatistics').mockReturnValue(of(mockStats));

    component.ngOnInit();

    expect(component.loading).toBe(false);
    expect(component.error).toBeTruthy();
  });

  it('should validate weight correctly: rejected if > 30 kg or <= 0', () => {
    const poidsControl = component.bagageForm.get('poids');

    poidsControl?.setValue(35.0);
    expect(poidsControl?.hasError('max')).toBe(true);
    expect(component.bagageForm.invalid).toBe(true);

    poidsControl?.setValue(-2.0);
    expect(poidsControl?.hasError('min')).toBe(true);
    expect(component.bagageForm.invalid).toBe(true);

    component.bagageForm.patchValue({
      type: 'Bagage en soute moyen (10–20 kg)',
      poids: 15.0
    });
    expect(poidsControl?.valid).toBe(true);
    expect(component.bagageForm.errors).toBeNull();
  });

  it('should reject category and weight inconsistency: cabin luggage cannot be 25 kg', () => {
    component.bagageForm.patchValue({
      reservationId: 'RES-101',
      type: 'Valise cabine (0–10 kg)',
      poids: 25.0,
      statut: 'REGISTERED'
    });
    expect(component.bagageForm.errors?.['categoryWeightMismatch']).toBeDefined();
    expect(component.bagageForm.invalid).toBe(true);

    // After switching to large hold luggage, 25 kg is valid
    component.bagageForm.patchValue({
      reservationId: 'RES-101',
      type: 'Grand bagage en soute (20–30 kg)',
      poids: 25.0,
      statut: 'REGISTERED'
    });
    expect(component.bagageForm.errors).toBeNull();
    expect(component.bagageForm.valid).toBe(true);
  });

  it('should calculate estimated price based on real backend rules', () => {
    component.bagageForm.patchValue({ poids: 8.0 });
    expect(component.estimatedPrice).toBe(30.0);

    component.bagageForm.patchValue({ poids: 18.0 });
    expect(component.estimatedPrice).toBe(50.0);

    component.bagageForm.patchValue({ poids: 28.0 });
    expect(component.estimatedPrice).toBe(70.0);

    component.bagageForm.patchValue({ poids: 32.0 });
    expect(component.estimatedPrice).toBeNull();
  });

  it('should associate a bagage to a reservation and reload data', () => {
    const target = mockBagages[0];
    const updated = { ...target, reservationId: 'RES-MODIFIED-99' };

    vi.spyOn(bagageService, 'associateToReservation').mockReturnValue(of(updated));
    const loadSpy = vi.spyOn(component, 'loadAllData');

    component.openAssociateModal(target);
    expect(component.showAssociateModal).toBe(true);

    component.targetReservationId = 'RES-MODIFIED-99';
    component.confirmAssociation();

    expect(bagageService.associateToReservation).toHaveBeenCalledWith('b1', 'RES-MODIFIED-99');
    expect(component.showAssociateModal).toBe(false);
    expect(loadSpy).toHaveBeenCalled();
  });

  describe('Form submission & Double-click prevention (saveBagage)', () => {
    it('should successfully create bagage, stop spinner via finalize, close modal, and reload data', () => {
      const createdBagage: Bagage = {
        id: 'new-b1',
        reservationId: 'RES-NEW',
        type: 'Valise cabine (0–10 kg)',
        poids: 8.0,
        statut: 'REGISTERED',
        prix: 30.0
      };

      const createSpy = vi.spyOn(bagageService, 'createBagage').mockReturnValue(of(createdBagage));
      const loadSpy = vi.spyOn(component, 'loadAllData');

      component.openCreateModal();
      expect(component.showFormModal).toBe(true);

      component.bagageForm.patchValue({
        reservationId: 'RES-NEW',
        type: 'Valise cabine (0–10 kg)',
        poids: 8.0,
        statut: 'REGISTERED'
      });

      component.saveBagage();

      expect(createSpy).toHaveBeenCalledWith({
        reservationId: 'RES-NEW',
        type: 'Valise cabine (0–10 kg)',
        poids: 8.0,
        statut: 'REGISTERED'
      });
      expect(component.submitting).toBe(false);
      expect(component.showFormModal).toBe(false);
      expect(component.toasts.length).toBeGreaterThan(0);
      expect(component.toasts[0].type).toBe('success');
      expect(loadSpy).toHaveBeenCalled();
    });

    it('should stop spinner via finalize and display explicit error when backend returns 400', () => {
      const errorResponse = {
        status: 400,
        error: {
          error: 'Bagage refusé : le poids dépasse la limite maximale autorisée de 30.0 kg.'
        }
      };

      vi.spyOn(bagageService, 'createBagage').mockReturnValue(throwError(() => errorResponse));

      component.openCreateModal();
      component.bagageForm.patchValue({
        reservationId: 'RES-FAIL',
        type: 'Grand bagage en soute (20–30 kg)',
        poids: 25.0,
        statut: 'REGISTERED'
      });

      component.saveBagage();

      expect(component.submitting).toBe(false); // finalize() must set submitting to false
      expect(component.showFormModal).toBe(true); // Modal remains open so user can correct
      expect(component.formError).toContain('Bagage refusé');
      expect(component.toasts.some(t => t.type === 'error')).toBe(true);
    });

    it('should ignore duplicate submission if already submitting', () => {
      const createSpy = vi.spyOn(bagageService, 'createBagage').mockReturnValue(of(mockBagages[0]));

      component.openCreateModal();
      component.bagageForm.patchValue({
        reservationId: 'RES-DUP',
        type: 'Valise cabine (0–10 kg)',
        poids: 8.0,
        statut: 'REGISTERED'
      });

      component.submitting = true; // Simulate in-flight submission
      component.saveBagage();

      // createBagage must NOT have been called again
      expect(createSpy).not.toHaveBeenCalled();
    });
  });

  it('should extract error message properly from backend validation map', () => {
    const backendError = {
      error: {
        poids: 'Le poids ne peut pas dépasser 30.0 kg'
      }
    };
    const extracted = component.extractErrorMessage(backendError);
    expect(extracted).toContain('poids : Le poids ne peut pas dépasser 30.0 kg');
  });

  it('should correctly count registered and pending bagages including French and English statuses', () => {
    component.bagages = [
      { id: '1', reservationId: 'R1', type: 'C', poids: 10, statut: 'REGISTERED' },
      { id: '2', reservationId: 'R2', type: 'C', poids: 12, statut: 'ENREGISTRE' },
      { id: '3', reservationId: 'R3', type: 'S', poids: 20, statut: 'PENDING' },
      { id: '4', reservationId: 'R4', type: 'S', poids: 22, statut: 'EN ATTENTE' }
    ];

    expect(component.registeredCount).toBe(2);
    expect(component.pendingCount).toBe(2);
  });

  it('should filter bagages correctly when clicking status filter pills', () => {
    component.bagages = [
      { id: '1', reservationId: 'R1', type: 'C', poids: 10, statut: 'REGISTERED' },
      { id: '2', reservationId: 'R2', type: 'C', poids: 12, statut: 'ENREGISTRE' },
      { id: '3', reservationId: 'R3', type: 'S', poids: 20, statut: 'PENDING' }
    ];

    component.onStatutFilterChange('REGISTER');
    expect(component.filteredBagages.length).toBe(2);

    component.onStatutFilterChange('PENDING');
    expect(component.filteredBagages.length).toBe(1);

    component.onStatutFilterChange('ALL');
    expect(component.filteredBagages.length).toBe(3);
  });

  it('should open detail, edit and delete modals on corresponding button clicks', () => {
    const bagage = mockBagages[0];

    component.openDetailModal(bagage);
    expect(component.showDetailModal).toBe(true);
    expect(component.selectedBagage).toBe(bagage);

    component.openEditModal(bagage);
    expect(component.showFormModal).toBe(true);
    expect(component.isEditMode).toBe(true);
    expect(component.currentBagageId).toBe(bagage.id);

    component.openDeleteModal(bagage);
    expect(component.showDeleteModal).toBe(true);
    expect(component.bagageToDelete).toBe(bagage);
  });

  describe('Interactive Stat Cards (filterByCard)', () => {
    beforeEach(() => {
      component.bagages = [
        { id: '1', reservationId: 'R1', type: 'C', poids: 10, statut: 'REGISTERED' },
        { id: '2', reservationId: 'R2', type: 'C', poids: 12, statut: 'ENREGISTRE' },
        { id: '3', reservationId: 'R3', type: 'S', poids: 20, statut: 'PENDING' },
        { id: '4', reservationId: 'R4', type: 'S', poids: 25, statut: 'EN ATTENTE' }
      ];
    });

    it('clicking Total card should show all bagages and reset search term', () => {
      component.searchTerm = 'R1';
      component.selectedStatutFilter = 'PENDING';

      component.filterByCard('ALL');

      expect(component.selectedStatutFilter).toBe('ALL');
      expect(component.searchTerm).toBe('');
      expect(component.filteredBagages.length).toBe(4);
    });

    it('clicking Validated card should filter to valid/registered bagages', () => {
      component.filterByCard('REGISTER');

      expect(component.selectedStatutFilter).toBe('REGISTER');
      expect(component.filteredBagages.length).toBe(2);
    });

    it('clicking Pending card should filter to pending bagages', () => {
      component.filterByCard('PENDING');

      expect(component.selectedStatutFilter).toBe('PENDING');
      expect(component.filteredBagages.length).toBe(2);
    });

    it('clicking already active card should toggle back to ALL', () => {
      component.selectedStatutFilter = 'REGISTER';
      component.filterByCard('REGISTER');

      expect(component.selectedStatutFilter).toBe('ALL');
      expect(component.filteredBagages.length).toBe(4);
    });
  });

  describe('Interactive Analytics Stat Cards (Poids total, Revenus, Poids moyen)', () => {
    beforeEach(() => {
      component.bagages = [
        { id: 'b1', reservationId: 'RES-01', type: 'Valise cabine', poids: 8.0, statut: 'REGISTERED', prix: 30.0 },
        { id: 'b2', reservationId: 'RES-02', type: 'Bagage moyen', poids: 16.0, statut: 'PENDING', prix: 50.0 },
        { id: 'b3', reservationId: 'RES-03', type: 'Grand bagage', poids: 26.0, statut: 'REGISTERED', prix: 70.0 }
      ];
    });

    it('should open and close Poids total modal and compute accurate weight breakdown', () => {
      expect(component.showWeightDetailModal).toBe(false);

      component.openWeightDetailModal();
      expect(component.showWeightDetailModal).toBe(true);

      const breakdown = component.weightBreakdown;
      expect(breakdown.totalWeight).toBe(50.0); // 8 + 16 + 26
      expect(breakdown.count).toBe(3);
      expect(breakdown.cabin.count).toBe(1);
      expect(breakdown.cabin.weight).toBe(8.0);
      expect(breakdown.medium.count).toBe(1);
      expect(breakdown.medium.weight).toBe(16.0);
      expect(breakdown.large.count).toBe(1);
      expect(breakdown.large.weight).toBe(26.0);
      expect(breakdown.items.length).toBe(3);
      expect(breakdown.items[0].pctOfMax).toBe(Math.round((8 / 30) * 100));

      component.closeWeightDetailModal();
      expect(component.showWeightDetailModal).toBe(false);
    });

    it('should open and close Revenus modal and compute accurate revenue tiers', () => {
      expect(component.showRevenueDetailModal).toBe(false);

      component.openRevenueDetailModal();
      expect(component.showRevenueDetailModal).toBe(true);

      const breakdown = component.revenueBreakdown;
      expect(breakdown.totalRevenue).toBe(150.0); // 30 + 50 + 70
      expect(breakdown.averagePerBagage).toBe(50.0);
      expect(breakdown.tier30.count).toBe(1);
      expect(breakdown.tier30.revenue).toBe(30.0);
      expect(breakdown.tier50.count).toBe(1);
      expect(breakdown.tier50.revenue).toBe(50.0);
      expect(breakdown.tier70.count).toBe(1);
      expect(breakdown.tier70.revenue).toBe(70.0);
      expect(breakdown.items.length).toBe(3);

      component.closeRevenueDetailModal();
      expect(component.showRevenueDetailModal).toBe(false);
    });

    it('should open and close Poids moyen modal and compute exact formula values', () => {
      expect(component.showAverageWeightDetailModal).toBe(false);

      component.openAverageWeightDetailModal();
      expect(component.showAverageWeightDetailModal).toBe(true);

      const details = component.averageWeightDetails;
      expect(details.totalWeight).toBe(50.0);
      expect(details.count).toBe(3);
      expect(details.average).toBe(Math.round((50.0 / 3) * 100) / 100); // 16.67
      expect(details.minWeight).toBe(8.0);
      expect(details.maxWeight).toBe(26.0);
      expect(details.maxAllowed).toBe(30.0);
      expect(details.marginToMax).toBe(Math.round((30.0 - 16.67) * 100) / 100);

      component.closeAverageWeightDetailModal();
      expect(component.showAverageWeightDetailModal).toBe(false);
    });
  });

  describe('Advanced Search, Type Filtering & Dynamic Sorting', () => {
    beforeEach(() => {
      component.bagages = [
        { id: 'b1', reservationId: 'RES-010', type: 'Valise cabine (0–10 kg)', poids: 8.0, statut: 'REGISTERED', prix: 30.0 },
        { id: 'b2', reservationId: 'RES-005', type: 'Bagage en soute moyen (10–20 kg)', poids: 18.0, statut: 'PENDING', prix: 50.0 },
        { id: 'b3', reservationId: 'RES-020', type: 'Grand bagage en soute (20–30 kg)', poids: 27.0, statut: 'REGISTERED', prix: 70.0 }
      ];
      component.filteredBagages = [...component.bagages];
    });

    it('should filter by luggage type (CABIN, MEDIUM, LARGE)', () => {
      component.onTypeFilterChange('CABIN');
      expect(component.filteredBagages.length).toBe(1);
      expect(component.filteredBagages[0].id).toBe('b1');

      component.onTypeFilterChange('MEDIUM');
      expect(component.filteredBagages.length).toBe(1);
      expect(component.filteredBagages[0].id).toBe('b2');

      component.onTypeFilterChange('LARGE');
      expect(component.filteredBagages.length).toBe(1);
      expect(component.filteredBagages[0].id).toBe('b3');

      component.onTypeFilterChange('ALL');
      expect(component.filteredBagages.length).toBe(3);
    });

    it('should sort dynamically by weight ascending and descending', () => {
      component.setSort('poids');
      expect(component.sortColumn).toBe('poids');
      expect(component.sortDirection).toBe('asc');
      expect(component.filteredBagages[0].poids).toBe(8.0);
      expect(component.filteredBagages[2].poids).toBe(27.0);

      // Toggle to descending
      component.setSort('poids');
      expect(component.sortDirection).toBe('desc');
      expect(component.filteredBagages[0].poids).toBe(27.0);
      expect(component.filteredBagages[2].poids).toBe(8.0);

      // Toggle to reset sort
      component.setSort('poids');
      expect(component.sortColumn).toBeNull();
    });

    it('should sort dynamically by reservationId', () => {
      component.setSort('reservationId');
      expect(component.sortColumn).toBe('reservationId');
      expect(component.sortDirection).toBe('asc');
      expect(component.filteredBagages[0].reservationId).toBe('RES-005');
      expect(component.filteredBagages[2].reservationId).toBe('RES-020');
    });

    it('should reset all filters and sort with resetAllFilters()', () => {
      component.searchTerm = 'cabine';
      component.selectedStatutFilter = 'REGISTER';
      component.selectedTypeFilter = 'CABIN';
      component.sortColumn = 'poids';
      component.sortDirection = 'desc';

      component.resetAllFilters();

      expect(component.searchTerm).toBe('');
      expect(component.selectedStatutFilter).toBe('ALL');
      expect(component.selectedTypeFilter).toBe('ALL');
      expect(component.sortColumn).toBeNull();
      expect(component.filteredBagages.length).toBe(3);
    });
  });

  describe('CSV Export & Session Operations History', () => {
    it('should export filtered bagages to Excel CSV format without throwing', () => {
      component.filteredBagages = [
        { id: 'b1', reservationId: 'RES-01', type: 'Valise cabine', poids: 8.0, statut: 'REGISTERED', prix: 30.0 }
      ];

      const toastSpy = vi.spyOn(component, 'showToast');
      component.exportToCsv();

      expect(toastSpy).toHaveBeenCalledWith('success', 'Export Excel réussi', expect.stringContaining('1 bagage(s) exporté(s)'));
    });

    it('should track session operations and allow clearing history', () => {
      component.openHistoryModal();
      expect(component.showHistoryModal).toBe(true);

      // Trigger an addition through saveBagage
      vi.spyOn(bagageService, 'createBagage').mockReturnValue(of({
        id: 'new-id-99',
        reservationId: 'RES-99',
        type: 'Valise cabine (0–10 kg)',
        poids: 9.0,
        statut: 'REGISTERED',
        prix: 30.0
      }));

      component.openCreateModal();
      component.bagageForm.patchValue({
        reservationId: 'RES-99',
        type: 'Valise cabine (0–10 kg)',
        poids: 9.0,
        statut: 'REGISTERED'
      });
      component.saveBagage();

      expect(component.operationsHistory.length).toBe(1);
      expect(component.operationsHistory[0].action).toBe('AJOUT');
      expect(component.operationsHistory[0].bagageId).toBe('new-id-99');

      component.clearHistory();
      expect(component.operationsHistory.length).toBe(0);

      component.closeHistoryModal();
      expect(component.showHistoryModal).toBe(false);
    });
  });
});
