import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService, UserProfile } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  username: string = '';
  password: string = '';
  showPassword: boolean = false;
  loading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  activeTab: 'express' | 'credentials' = 'express';

  currentUser: UserProfile | null = null;
  returnUrl: string = '/';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  setTab(tab: 'express' | 'credentials'): void {
    this.activeTab = tab;
    this.errorMessage = null;
    this.cdr.detectChanges();
  }

  ngOnInit(): void {
    const rawReturn = this.route.snapshot.queryParams['returnUrl'];
    if (rawReturn) {
      try {
        this.returnUrl = decodeURIComponent(rawReturn);
      } catch {
        this.returnUrl = rawReturn;
      }
    } else {
      this.returnUrl = '/';
    }

    if (this.returnUrl.startsWith('/login')) {
      this.returnUrl = '/';
    }

    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.cdr.detectChanges();
    });

    // Si l'utilisateur est déjà authentifié avec une session valide et qu'une URL cible est demandée
    if (this.authService.isAuthenticated() && this.returnUrl && this.returnUrl !== '/' && !this.returnUrl.startsWith('/login')) {
      this.successMessage = `Session active détectée. Redirection vers votre destination...`;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.continueToReturnUrl();
      }, 500);
    }
  }

  continueToReturnUrl(): void {
    if (this.returnUrl && this.returnUrl !== '/' && !this.returnUrl.startsWith('/login')) {
      this.router.navigateByUrl(this.returnUrl);
    } else {
      this.router.navigate(['/']);
    }
  }

  loginAs(userType: 'admin' | 'voyageur'): void {
    if (userType === 'admin') {
      this.username = 'admin';
      this.password = 'admin';
    } else {
      this.username = 'voyageur';
      this.password = 'user';
    }
    this.performLogin();
  }

  performLogin(): void {
    if (!this.username || !this.password) {
      this.errorMessage = 'Veuillez saisir un identifiant et un mot de passe.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.authService.login(this.username, this.password).subscribe({
      next: (res) => {
        this.loading = false;
        const role = this.authService.isAdmin() ? 'Administrateur' : 'Voyageur';
        this.successMessage = `Connexion réussie ! Bienvenue, ${this.username} (${role}). Redirection vers votre destination...`;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.continueToReturnUrl();
        }, 500);
      },
      error: (err) => {
        this.loading = false;
        console.error('Erreur connexion Keycloak:', err);
        this.errorMessage = 'Échec de la connexion auprès de Keycloak (identifiants invalides ou service injoignable).';
        this.cdr.detectChanges();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.successMessage = 'Vous avez été déconnecté avec succès.';
    this.errorMessage = null;
    this.cdr.detectChanges();
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
