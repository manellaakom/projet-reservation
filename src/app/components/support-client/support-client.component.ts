import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { SupportRequestService } from '../../services/support-request.service';
import { SupportRequest, SupportRequestDto, SupportStats, SupportStatus } from '../../models/support-request.model';

interface ToastNotification {
  id: number;
  type: 'success' | 'error' | 'info';
  title: string;
  message: string;
}

@Component({
  selector: 'app-support-client',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './support-client.component.html',
  styleUrls: ['./support-client.component.scss']
})
export class SupportClientComponent implements OnInit {
  // Liste des requêtes
  requests: SupportRequest[] = [];
  filteredRequests: SupportRequest[] = [];

  // Statistiques
  stats: SupportStats = {
    total: 0,
    enAttente: 0,
    enCours: 0,
    resolu: 0,
    ferme: 0
  };

  // États UI
  loading = false;
  submitting = false;
  deleting = false;

  // Filtres
  searchQuery = '';
  selectedStatus = 'ALL';
  reservationFilter: number | null = null;
  clientFilter: number | null = null;

  // Modals
  showFormModal = false;
  isEditMode = false;
  editingId: number | null = null;

  showDetailModal = false;
  selectedRequest: SupportRequest | null = null;

  showDeleteModal = false;
  requestToDelete: SupportRequest | null = null;

  // Formulaire réactif
  requestForm!: FormGroup;

  // Toasts
  toasts: ToastNotification[] = [];
  private toastCounter = 0;

  // Statuts disponibles
  readonly statuses = [
    { value: SupportStatus.EN_ATTENTE, label: 'En attente', badgeClass: 'badge-warning', icon: 'pi pi-clock' },
    { value: SupportStatus.EN_COURS, label: 'En cours', badgeClass: 'badge-info', icon: 'pi pi-sync' },
    { value: SupportStatus.RESOLU, label: 'Résolu', badgeClass: 'badge-success', icon: 'pi pi-check-circle' },
    { value: SupportStatus.FERME, label: 'Fermé', badgeClass: 'badge-secondary', icon: 'pi pi-lock' }
  ];

  // Catégories suggérées
  readonly categories = [
    'Bagage perdu ou retardé',
    'Bagage endommagé',
    'Excédent de bagage',
    'Réservation & Billet',
    'Remboursement & Paiement',
    'Assistance spéciale',
    'Réclamation générale'
  ];

  constructor(
    private supportService: SupportRequestService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadData();
  }

  initForm(): void {
    this.requestForm = this.fb.group({
      sujet: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]],
      categorie: ['Bagage perdu ou retardé', [Validators.required, Validators.maxLength(100)]],
      status: [SupportStatus.EN_ATTENTE],
      description: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(1000)]],
      clientId: [null, [Validators.min(1)]],
      reservationId: [null, [Validators.min(1)]],
      agentId: [null, [Validators.min(1)]]
    });
  }

  loadData(): void {
    this.loading = true;
    this.supportService.getAll({
      status: this.selectedStatus,
      search: this.searchQuery,
      reservationId: this.reservationFilter || undefined,
      clientId: this.clientFilter || undefined
    })
    .pipe(finalize(() => {
      this.loading = false;
      this.cdr.markForCheck();
    }))
    .subscribe({
      next: (data) => {
        this.requests = data || [];
        this.applyLocalFilters();
        this.computeStats();
      },
      error: (err) => {
        console.error('Erreur de chargement des demandes de support :', err);
        this.addToast('error', 'Erreur de connexion', 'Impossible de joindre le service de support client (Backend Symfony). Vérifiez que le service tourne.');
      }
    });

    // Charger les stats depuis le backend si dispo
    this.supportService.getStats().subscribe({
      next: (s) => {
        if (s) this.stats = s;
      },
      error: () => {
        // En cas d'erreur de route stats, le calcul local computeStats prend le relais
      }
    });
  }

  computeStats(): void {
    const total = this.requests.length;
    let enAttente = 0;
    let enCours = 0;
    let resolu = 0;
    let ferme = 0;

    this.requests.forEach(r => {
      const st = (r.status || '').toString().toUpperCase();
      if (st.includes('ATTENTE')) enAttente++;
      else if (st.includes('COURS')) enCours++;
      else if (st.includes('RESOLU')) resolu++;
      else if (st.includes('FERME')) ferme++;
    });

    this.stats = { total, enAttente, enCours, resolu, ferme };
  }

  applyLocalFilters(): void {
    let res = [...this.requests];

    if (this.selectedStatus && this.selectedStatus !== 'ALL') {
      res = res.filter(r => r.status === this.selectedStatus);
    }

    if (this.searchQuery && this.searchQuery.trim() !== '') {
      const q = this.searchQuery.toLowerCase().trim();
      res = res.filter(r =>
        (r.sujet && r.sujet.toLowerCase().includes(q)) ||
        (r.description && r.description.toLowerCase().includes(q)) ||
        (r.categorie && r.categorie.toLowerCase().includes(q)) ||
        (r.id && r.id.toString().includes(q))
      );
    }

    if (this.reservationFilter) {
      res = res.filter(r => r.reservationId === this.reservationFilter);
    }

    if (this.clientFilter) {
      res = res.filter(r => r.clientId === this.clientFilter);
    }

    this.filteredRequests = res;
  }

  onFilterChange(): void {
    this.applyLocalFilters();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedStatus = 'ALL';
    this.reservationFilter = null;
    this.clientFilter = null;
    this.loadData();
  }

  // --- MODALS ACTIONS ---

  openCreateModal(): void {
    this.isEditMode = false;
    this.editingId = null;
    this.requestForm.reset({
      sujet: '',
      categorie: 'Bagage perdu ou retardé',
      status: SupportStatus.EN_ATTENTE,
      description: '',
      clientId: null,
      reservationId: null,
      agentId: null
    });
    this.showFormModal = true;
  }

  openEditModal(req: SupportRequest): void {
    this.isEditMode = true;
    this.editingId = req.id!;
    this.requestForm.patchValue({
      sujet: req.sujet,
      categorie: req.categorie,
      status: req.status,
      description: req.description,
      clientId: req.clientId ?? null,
      reservationId: req.reservationId ?? null,
      agentId: req.agentId ?? null
    });
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.isEditMode = false;
    this.editingId = null;
    this.requestForm.reset();
  }

  openDetailModal(req: SupportRequest): void {
    this.selectedRequest = req;
    this.showDetailModal = true;
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedRequest = null;
  }

  openDeleteModal(req: SupportRequest): void {
    this.requestToDelete = req;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.requestToDelete = null;
  }

  // --- CRUD OPERATIONS ---

  onSubmit(): void {
    if (this.requestForm.invalid) {
      this.requestForm.markAllAsTouched();
      this.addToast('error', 'Formulaire incomplet', 'Veuillez renseigner tous les champs obligatoires.');
      return;
    }

    this.submitting = true;
    const formVal = this.requestForm.value;

    const dto: SupportRequestDto = {
      sujet: formVal.sujet.trim(),
      categorie: formVal.categorie,
      description: formVal.description.trim(),
      status: formVal.status,
      clientId: formVal.clientId ? Number(formVal.clientId) : null,
      reservationId: formVal.reservationId ? Number(formVal.reservationId) : null,
      agentId: formVal.agentId ? Number(formVal.agentId) : null
    };

    if (this.isEditMode && this.editingId) {
      this.supportService.update(this.editingId, dto)
        .pipe(finalize(() => {
          this.submitting = false;
          this.cdr.markForCheck();
        }))
        .subscribe({
          next: (updated) => {
            this.addToast('success', 'Demande mise à jour', `La demande #${updated.id} a été modifiée avec succès.`);
            this.closeFormModal();
            this.loadData();
          },
          error: (err) => {
            console.error('Erreur mise à jour :', err);
            this.addToast('error', 'Erreur de modification', err?.error?.error || 'Impossible de mettre à jour la demande.');
          }
        });
    } else {
      this.supportService.create(dto)
        .pipe(finalize(() => {
          this.submitting = false;
          this.cdr.markForCheck();
        }))
        .subscribe({
          next: (created) => {
            this.addToast('success', 'Demande créée', `Nouvelle demande #${created.id} enregistrée.`);
            this.closeFormModal();
            this.loadData();
          },
          error: (err) => {
            console.error('Erreur création :', err);
            this.addToast('error', 'Erreur de création', err?.error?.error || 'Impossible de créer la demande de support.');
          }
        });
    }
  }

  onQuickStatusChange(req: SupportRequest, newStatus: string): void {
    if (!req.id || req.status === newStatus) return;

    this.supportService.updateStatus(req.id, newStatus).subscribe({
      next: (updated) => {
        req.status = updated.status;
        req.dateMiseAJour = updated.dateMiseAJour;
        this.computeStats();
        this.addToast('info', 'Statut mis à jour', `Demande #${req.id} passée à "${this.getStatusLabel(newStatus)}".`);
      },
      error: (err) => {
        console.error('Erreur changement statut :', err);
        this.addToast('error', 'Erreur', 'Impossible de mettre à jour le statut.');
      }
    });
  }

  confirmDelete(): void {
    if (!this.requestToDelete?.id) return;

    this.deleting = true;
    const id = this.requestToDelete.id;

    this.supportService.delete(id)
      .pipe(finalize(() => {
        this.deleting = false;
        this.closeDeleteModal();
        this.cdr.markForCheck();
      }))
      .subscribe({
        next: () => {
          this.addToast('success', 'Suppression réussie', `La demande #${id} a été supprimée définitivement.`);
          this.loadData();
        },
        error: (err) => {
          console.error('Erreur suppression :', err);
          this.addToast('error', 'Erreur de suppression', err?.error?.error || 'Impossible de supprimer la demande.');
        }
      });
  }

  // --- HELPERS ---

  getStatusLabel(status: string | undefined): string {
    const s = this.statuses.find(item => item.value === status);
    return s ? s.label : status || 'Inconnu';
  }

  getStatusBadgeClass(status: string | undefined): string {
    const s = this.statuses.find(item => item.value === status);
    return s ? s.badgeClass : 'badge-secondary';
  }

  getStatusIcon(status: string | undefined): string {
    const s = this.statuses.find(item => item.value === status);
    return s ? s.icon : 'pi pi-question-circle';
  }

  addToast(type: 'success' | 'error' | 'info', title: string, message: string): void {
    const toast: ToastNotification = {
      id: ++this.toastCounter,
      type,
      title,
      message
    };
    this.toasts.push(toast);

    setTimeout(() => {
      this.removeToast(toast.id);
    }, 4500);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.cdr.markForCheck();
  }
}
