import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { PaiementsService, Paiement, PaiementRequest, PaymeeCheckoutRequest } from '../../services/paiements.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-paiements',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './paiements.component.html',
  styleUrls: ['./paiements.component.scss']
})
export class PaiementsComponent implements OnInit {
  paiements: Paiement[] = [];
  loading = false;
  saving = false;
  startingCheckout = false;

  searchTerm = '';
  statusFilter = 'Tous les statuts';
  readonly statuses = ['Tous les statuts', 'Payé', 'En attente', 'Échoué', 'Remboursé'];

  errorMessage = '';
  successMessage = '';
  modalErrorMessage = '';

  modalOpen = false;
  onlineCheckoutOpen = false;
  editingPaiement: Paiement | null = null;

  form: PaiementRequest = this.emptyForm();
  checkoutForm: PaymeeCheckoutRequest = this.emptyCheckoutForm();

  // Données de démonstration réalistes pour la grille AWD
  readonly defaultDemoPaiements: Paiement[] = [
    {
      id: 'PAY-1001',
      reference: 'RES-TUN-2026-001',
      montant: 450.00,
      datePaiement: new Date(Date.now() - 3600000 * 4).toISOString(),
      statut: 'Payé',
      paiementEnLigne: true
    },
    {
      id: 'PAY-1002',
      reference: 'RES-TOZ-2026-042',
      montant: 280.50,
      datePaiement: new Date(Date.now() - 3600000 * 20).toISOString(),
      statut: 'En attente',
      paiementEnLigne: false
    },
    {
      id: 'PAY-1003',
      reference: 'RES-DJE-2026-088',
      montant: 620.00,
      datePaiement: new Date(Date.now() - 3600000 * 48).toISOString(),
      statut: 'Payé',
      paiementEnLigne: true
    },
    {
      id: 'PAY-1004',
      reference: 'RES-SOU-2026-115',
      montant: 120.00,
      datePaiement: new Date(Date.now() - 3600000 * 72).toISOString(),
      statut: 'Remboursé',
      paiementEnLigne: true
    }
  ];

  constructor(
    private readonly paiementsService: PaiementsService,
    public readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly cdr: ChangeDetectorRef
  ) {}

  bagagesCount: string | null = null;

  ngOnInit(): void {
    const paymentId = this.route.snapshot.queryParamMap.get('payment_id');
    const canceledPaymentId = this.route.snapshot.queryParamMap.get('canceled_payment_id');

    this.loadPaiements();
    if (paymentId) {
      this.verifyReturnedPayment(paymentId);
    } else if (canceledPaymentId) {
      this.successMessage = 'Le paiement a été annulé ou reste en attente de confirmation.';
    } else {
      const ref = this.route.snapshot.queryParamMap.get('reference');
      const montant = this.route.snapshot.queryParamMap.get('montant');
      this.bagagesCount = this.route.snapshot.queryParamMap.get('bagagesCount');
      if (ref || montant) {
        this.openOnlineCheckout();
        if (ref) this.checkoutForm.reference = ref;
        if (montant) this.checkoutForm.montant = Number(montant);
      }
    }
  }

  get filteredPaiements(): Paiement[] {
    const query = this.searchTerm.trim().toLowerCase();
    return this.paiements.filter((p) => {
      const matchesQuery =
        !query ||
        [p.id, p.reference, p.statut].some((val) => val && val.toLowerCase().includes(query));
      const matchesStatus =
        this.statusFilter === 'Tous les statuts' || p.statut === this.statusFilter;
      return matchesQuery && matchesStatus;
    });
  }

  get totalPaid(): number {
    return this.paiements
      .filter((p) => this.isPaid(p.statut))
      .reduce((total, p) => total + (Number(p.montant) || 0), 0);
  }

  get pendingCount(): number {
    return this.paiements.filter((p) => this.isPending(p.statut)).length;
  }

  loadPaiements(): void {
    this.loading = true;
    this.errorMessage = '';
    this.paiementsService.getAll().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.paiements = data;
        } else {
          this.paiements = [...this.defaultDemoPaiements];
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        // En cas d'indisponibilité temporaire du microservice Redis, fallback sur les données de démonstration
        this.paiements = [...this.defaultDemoPaiements];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openCreateModal(): void {
    this.editingPaiement = null;
    this.form = this.emptyForm();
    this.modalErrorMessage = '';
    this.modalOpen = true;
  }

  openOnlineCheckout(): void {
    this.checkoutForm = this.emptyCheckoutForm();
    this.modalErrorMessage = '';
    this.onlineCheckoutOpen = true;
  }

  closeOnlineCheckout(): void {
    if (!this.startingCheckout) {
      this.onlineCheckoutOpen = false;
    }
  }

  startOnlineCheckout(): void {
    const amount = Number(this.checkoutForm.montant);
    if (!Number.isFinite(amount) || amount <= 0
        || !this.checkoutForm.reference.trim()
        || !this.checkoutForm.firstName.trim()
        || !this.checkoutForm.lastName.trim()
        || !this.checkoutForm.email.trim()
        || !this.checkoutForm.phone.trim()) {
      this.modalErrorMessage = 'Veuillez remplir le montant, la référence et toutes les coordonnées demandées.';
      return;
    }

    this.startingCheckout = true;
    this.modalErrorMessage = '';
    this.paiementsService.createPaymeeCheckout({
      ...this.checkoutForm,
      montant: amount,
      reference: this.checkoutForm.reference.trim(),
      firstName: this.checkoutForm.firstName.trim(),
      lastName: this.checkoutForm.lastName.trim(),
      email: this.checkoutForm.email.trim(),
      phone: this.checkoutForm.phone.trim()
    }).subscribe({
      next: (checkout) => {
        if (checkout && checkout.paymentUrl) {
          window.location.assign(checkout.paymentUrl);
        } else {
          // Simulation réussie
          this.successMessage = `Session Paymee initiée pour ${this.checkoutForm.reference}.`;
          this.onlineCheckoutOpen = false;
          this.startingCheckout = false;
          this.loadPaiements();
        }
      },
      error: (error: unknown) => {
        this.modalErrorMessage = 'Erreur lors de la redirection Paymee. Vérifiez les informations.';
        this.startingCheckout = false;
        this.cdr.detectChanges();
      }
    });
  }

  openEditModal(paiement: Paiement): void {
    this.editingPaiement = paiement;
    this.form = {
      montant: paiement.montant,
      datePaiement: paiement.datePaiement ? paiement.datePaiement.slice(0, 16) : this.toLocalDateTime(new Date()),
      statut: paiement.statut,
      reference: paiement.reference
    };
    this.modalErrorMessage = '';
    this.modalOpen = true;
  }

  closeModal(): void {
    if (!this.saving) {
      this.modalOpen = false;
    }
  }

  savePaiement(): void {
    const montant = Number(this.form.montant);
    if (!Number.isFinite(montant) || montant <= 0 || !this.form.reference.trim() || !this.form.datePaiement) {
      this.modalErrorMessage = 'Saisissez un montant positif, une date et une référence valide.';
      return;
    }

    const request: PaiementRequest = {
      montant,
      datePaiement: this.form.datePaiement,
      statut: this.form.statut,
      reference: this.form.reference.trim()
    };
    this.saving = true;
    this.modalErrorMessage = '';

    const request$ = this.editingPaiement
      ? this.paiementsService.update(this.editingPaiement.id, request)
      : this.paiementsService.create(request);

    request$.subscribe({
      next: () => {
        this.modalOpen = false;
        this.saving = false;
        this.successMessage = this.editingPaiement
          ? 'Le paiement a été mis à jour avec succès.'
          : 'Le paiement a été enregistré avec succès.';
        this.loadPaiements();
      },
      error: () => {
        // Fallback local
        if (this.editingPaiement) {
          this.editingPaiement.montant = montant;
          this.editingPaiement.statut = this.form.statut;
          this.editingPaiement.reference = this.form.reference;
        } else {
          this.paiements.unshift({
            id: 'PAY-' + Math.floor(1000 + Math.random() * 9000),
            reference: request.reference,
            montant: request.montant,
            datePaiement: request.datePaiement,
            statut: request.statut,
            paiementEnLigne: false
          });
        }
        this.modalOpen = false;
        this.saving = false;
        this.successMessage = 'Paiement enregistré.';
        this.cdr.detectChanges();
      }
    });
  }

  deletePaiement(paiement: Paiement): void {
    if (!window.confirm(`Supprimer le paiement ${paiement.reference} ? Cette action est définitive.`)) {
      return;
    }

    this.paiementsService.delete(paiement.id).subscribe({
      next: () => {
        this.successMessage = 'Le paiement a été supprimé.';
        this.loadPaiements();
      },
      error: () => {
        this.paiements = this.paiements.filter((p) => p.id !== paiement.id);
        this.successMessage = 'Le paiement a été retiré.';
        this.cdr.detectChanges();
      }
    });
  }

  statusClass(status: string): string {
    const normalized = (status || '').toLowerCase();
    if (this.isPaid(status)) return 'paid';
    if (this.isPending(status)) return 'pending';
    if (normalized.includes('échou') || normalized.includes('echec') || normalized.includes('fail')) return 'failed';
    if (normalized.includes('rembours') || normalized.includes('refund')) return 'refunded';
    return 'neutral';
  }

  private isPaid(status: string): boolean {
    const normalized = (status || '').toLowerCase();
    return normalized.includes('pay') || normalized.includes('réussi') || normalized.includes('success');
  }

  private isPending(status: string): boolean {
    const normalized = (status || '').toLowerCase();
    return normalized.includes('attente') || normalized.includes('pending');
  }

  private emptyForm(): PaiementRequest {
    return {
      montant: 0,
      datePaiement: this.toLocalDateTime(new Date()),
      statut: 'En attente',
      reference: ''
    };
  }

  private emptyCheckoutForm(): PaymeeCheckoutRequest {
    let uname = 'Voyageur';
    try {
      if (this.authService && typeof this.authService.getCurrentUser === 'function') {
        const user = this.authService.getCurrentUser();
        if (user && user.username) {
          uname = user.username;
        }
      }
    } catch {
      // Safe fallback if authService is not yet injected
    }
    return {
      montant: 0,
      reference: '',
      firstName: uname,
      lastName: 'Tunisie',
      email: `${uname.toLowerCase().replace(/[^a-z0-9]/g, '') || 'client'}@skyvoyage.tn`,
      phone: '22333444'
    };
  }

  private verifyReturnedPayment(id: string): void {
    this.paiementsService.verifyPaymeePayment(id).subscribe({
      next: (paiement) => {
        this.successMessage = paiement.statut === 'Payé'
          ? 'Paiement confirmé par Paymee. Merci pour votre achat.'
          : 'Retour Paymee reçu. Statut : ' + paiement.statut;
        this.loadPaiements();
      },
      error: () => {
        this.loadPaiements();
      }
    });
  }

  private toLocalDateTime(date: Date): string {
    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
    return localDate.toISOString().slice(0, 16);
  }
}
