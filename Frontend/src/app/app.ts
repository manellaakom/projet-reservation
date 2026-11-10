import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, RouterModule, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService, UserProfile } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App implements OnInit {
  title = 'SkyVoyage — Plateforme de Voyages & Bagages';
  currentUser: UserProfile | null = null;
  isLoginPage: boolean = false;

  constructor(
    public authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // 1. Abonnement à l'utilisateur courant
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.checkLayoutState(this.router.url);
      this.cdr.detectChanges();
    });

    // 2. Détection du changement d'URL pour basculer entre layout extérieur (login) et intérieur (app)
    this.checkLayoutState(this.router.url);
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.checkLayoutState(event.urlAfterRedirects || event.url);
      this.cdr.detectChanges();
    });
  }

  private checkLayoutState(url: string): void {
    // Si l'utilisateur n'est pas connecté ou se trouve sur /login, on affiche le layout extérieur
    const onLoginUrl = url ? url.startsWith('/login') : false;
    this.isLoginPage = onLoginUrl || !this.authService.isAuthenticated();
  }

  logout(): void {
    this.authService.logout();
    this.isLoginPage = true;
    this.router.navigate(['/login']);
    this.cdr.detectChanges();
  }
}
