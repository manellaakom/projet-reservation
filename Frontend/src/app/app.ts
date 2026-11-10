import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { KeycloakService } from './keycloak.service';
import {
  Paiement,
  PaiementRequest,
  PaiementsService,
  PaymeeCheckoutRequest
} from './paiements.service';

type View = 'home' | 'paiements' | 'reservations';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  currentView: View = 'home';
  paiements: Paiement[] = [];
  searchTerm = '';
  statusFilter = 'Tous les statuts';
  loading = true;
  saving = false;
  startingCheckout = false;
  modalOpen = false;
  onlineCheckoutOpen = false;
  errorMessage = '';
  modalErrorMessage = '';
  successMessage = '';
  editingPaiement: Paiement | null = null;
  readonly statuses = ['En attente', 'Payé', 'Échoué', 'Remboursé'];
  form: PaiementRequest = this.emptyForm();
  checkoutForm: PaymeeCheckoutRequest = this.emptyCheckoutForm();

  constructor(
    private readonly paiementsService: PaiementsService,
    readonly keycloak: KeycloakService
  ) {}

  ngOnInit(): void {
    const returnUrl = new URL(window.location.href);
    const paymentId = returnUrl.searchParams.get('paiement');
    const canceledPaymentId = returnUrl.searchParams.get('paiementAnnule');
    if (paymentId || canceledPaymentId) {
      returnUrl.searchParams.delete('paiement');
      returnUrl.searchParams.delete('paiementAnnule');
      window.history.replaceState({}, '', `${returnUrl.pathname}${returnUrl.search}${returnUrl.hash}`);
      this.currentView = 'paiements';
    }

    this.loadPaiements();
    if (paymentId) {
      this.verifyReturnedPayment(paymentId);
    } else if (canceledPaymentId) {
      this.successMessage = 'Le paiement a été annulé. Il reste en attente de confirmation.';
    }
  }

  get filteredPaiements(): Paiement[] {
    const query = this.searchTerm.trim().toLocaleLowerCase();
    return this.paiements.filter((paiement) => {
      const matchesQuery =
        !query ||
        [paiement.id, paiement.reference, paiement.statut]
          .some((value) => value.toLocaleLowerCase().includes(query));
      const matchesStatus =
        this.statusFilter === 'Tous les statuts' || paiement.statut === this.statusFilter;
      return matchesQuery && matchesStatus;
    });
  }

  get totalPaid(): number {
    return this.paiements
      .filter((paiement) => this.isPaid(paiement.statut))
      .reduce((total, paiement) => total + paiement.montant, 0);
  }

  get pendingCount(): number {
    return this.paiements.filter((paiement) => this.isPending(paiement.statut)).length;
  }

  loadPaiements(): void {
    this.loading = true;
    this.errorMessage = '';
    this.paiementsService.getAll().subscribe({
      next: (paiements) => {
        this.paiements = paiements;
        this.loading = false;
      },
      error: (error: unknown) => {
        this.errorMessage = this.describeError(error);
        this.loading = false;
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
      this.modalErrorMessage = 'Renseignez le montant, la référence et les coordonnées demandées.';
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
        window.location.assign(checkout.paymentUrl);
      },
      error: (error: unknown) => {
        this.modalErrorMessage = this.describeError(error);
        this.startingCheckout = false;
      }
    });
  }

  openEditModal(paiement: Paiement): void {
    this.editingPaiement = paiement;
    this.form = {
      montant: paiement.montant,
      datePaiement: paiement.datePaiement.slice(0, 16),
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
      this.modalErrorMessage = 'Saisissez un montant positif, une date et une référence.';
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
          ? 'Le paiement a été mis à jour.'
          : 'Le paiement a été enregistré.';
        this.loadPaiements();
      },
      error: (error: unknown) => {
        this.modalErrorMessage = this.describeError(error);
        this.saving = false;
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
      error: (error: unknown) => {
        this.errorMessage = this.describeError(error);
      }
    });
  }

  showView(view: View): void {
    this.currentView = view;
    this.successMessage = '';
  }

  statusClass(status: string): string {
    const normalized = status.toLocaleLowerCase();
    if (this.isPaid(status)) {
      return 'paid';
    }
    if (this.isPending(status)) {
      return 'pending';
    }
    if (normalized.includes('échou') || normalized.includes('echec') || normalized.includes('fail')) {
      return 'failed';
    }
    if (normalized.includes('rembours') || normalized.includes('refund')) {
      return 'refunded';
    }
    return 'neutral';
  }

  private isPaid(status: string): boolean {
    const normalized = status.toLocaleLowerCase();
    return normalized.includes('pay') || normalized.includes('réussi') || normalized.includes('success');
  }

  private isPending(status: string): boolean {
    const normalized = status.toLocaleLowerCase();
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
    return {
      montant: 0,
      reference: '',
      firstName: '',
      lastName: '',
      email: '',
      phone: ''
    };
  }

  private verifyReturnedPayment(id: string): void {
    this.paiementsService.verifyPaymeePayment(id).subscribe({
      next: (paiement) => {
        this.successMessage = paiement.statut === 'Payé'
          ? 'Paiement confirmé par Paymee. Merci pour votre achat.'
          : 'Retour de Paymee reçu. Le paiement est en attente de confirmation.';
        this.loadPaiements();
      },
      error: (error: unknown) => {
        this.loadPaiements();
        this.errorMessage = this.describeError(error);
      }
    });
  }

  private toLocalDateTime(date: Date): string {
    const localDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
    return localDate.toISOString().slice(0, 16);
  }

  private describeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return 'Impossible de joindre le gateway sur le port 8088. Démarrez Redis, Eureka, le service Paiement et le gateway, puis réessayez.';
      }
      const body: unknown = error.error;
      if (typeof body === 'string' && body.trim()) {
        if (/<\/?[a-z][\s\S]*>/i.test(body)) {
          return error.status === 401
            ? 'Le serveur a refusé la requête (HTTP 401). Vérifiez la configuration du gateway.'
            : `La requête a échoué (HTTP ${error.status}).`;
        }
        return body.trim();
      }
      if (body && typeof body === 'object') {
        const messages = Object.values(body as Record<string, unknown>)
          .filter((value): value is string => typeof value === 'string' && !!value.trim());
        if (messages.length) {
          return messages.join(' ');
        }
      }
      return `La requête a échoué (HTTP ${error.status}).`;
    }
    return 'Une erreur inattendue est survenue.';
  }
}
