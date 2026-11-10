import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
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

  constructor(public authService: AuthService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.cdr.detectChanges();
    });
  }

  logout(): void {
    this.authService.logout();
    this.cdr.detectChanges();
  }
}
