import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach } from 'vitest';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the home component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the core features list', () => {
    expect(component.features.length).toBeGreaterThanOrEqual(5);
    const featureTitles = component.features.map(f => f.title);
    expect(featureTitles).toContain('Gestion Centralisée des Bagages');
    expect(featureTitles).toContain('Tarification Automatique');
    expect(featureTitles).toContain('Contrôle & Sécurité de Charge');
  });

  it('should render the hero title and CTA link in DOM', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const heroTitle = compiled.querySelector('.hero-title')?.textContent;
    expect(heroTitle).toContain('Simplifiez la gestion');
    expect(heroTitle).toContain('de vos bagages');

    const ctaButton = compiled.querySelector('.btn-hero-primary');
    expect(ctaButton?.textContent).toContain('Accéder à mon espace');
  });
});
