import { Component, computed, effect, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CurrentUserService } from '../../core/auth/current-user.service';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';

const HIGHLIGHTS = [
  {
    title: 'Réservation de chambre',
    body: "Consultez la disponibilité et réservez votre chambre à la résidence en quelques clics.",
  },
  {
    title: 'Suivi des réclamations',
    body: "Signalez un problème et suivez son traitement du dépôt jusqu'à la résolution.",
  },
  {
    title: 'Documents centralisés',
    body: "CIN, diplôme, photo de profil — envoyez vos documents et suivez leur validation.",
  },
];

/**
 * Public landing page for unauthenticated guests (Task A1) — replaces the
 * old bare "Not signed in." screen. Authenticated users never linger here:
 * the effect below sends them straight to their shell (ADMINISTRATEUR ->
 * /admin, everyone else -> /student), and the CTA cards double as an
 * explicit, always-visible navigation entry back to that dashboard in case
 * someone lands on `/` again later.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, ThemeToggleComponent],
  template: `
    <div class="min-h-screen bg-surface dark:bg-surface-dark">
      @if (currentUser.authenticated()) {
        <div class="flex min-h-screen flex-col items-center justify-center gap-4 px-4 text-center">
          <p class="text-sm text-neutral-500 dark:text-neutral-400">Redirection en cours…</p>
          <a
            [routerLink]="destination()"
            class="rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
          >
            Continuer vers {{ destination() === '/admin' ? "l'espace admin" : "l'espace étudiant" }}
          </a>
        </div>
      } @else {
        <header class="flex items-center justify-between px-6 py-4 sm:px-10">
          <div class="flex items-center gap-2">
            <span class="text-lg font-semibold text-primary-700 dark:text-primary-300">ResiAIAC</span>
          </div>
          <div class="flex items-center gap-3">
            <app-theme-toggle></app-theme-toggle>
            <button
              type="button"
              (click)="currentUser.login()"
              class="rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
            >
              Se connecter
            </button>
          </div>
        </header>

        <main class="mx-auto flex max-w-5xl flex-col items-center gap-16 px-6 pb-24 pt-10 text-center sm:px-10">
          <section class="flex flex-col items-center gap-5">
            <span class="rounded-full bg-primary-50 px-3 py-1 text-xs font-medium text-primary-700 dark:bg-primary-900/40 dark:text-primary-300">
              Résidence universitaire AIAC
            </span>
            <h1 class="max-w-2xl text-3xl font-semibold tracking-tight text-neutral-900 dark:text-white sm:text-4xl">
              La gestion de votre internat, simplifiée
            </h1>
            <p class="max-w-xl text-base text-neutral-500 dark:text-neutral-400">
              Réservez votre chambre, envoyez vos documents et suivez vos réclamations — tout au même
              endroit, pensé pour les étudiants et le personnel de la résidence AIAC.
            </p>
            <button
              type="button"
              (click)="currentUser.login()"
              class="mt-2 rounded-md bg-primary-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-primary-700"
            >
              Se connecter pour commencer
            </button>
          </section>

          <section class="grid w-full grid-cols-1 gap-4 sm:grid-cols-3">
            @for (item of highlights; track item.title) {
              <div class="rounded-xl border border-neutral-100 bg-surface p-5 text-left shadow-sm dark:border-white/10 dark:bg-white/5">
                <h2 class="text-sm font-semibold text-neutral-900 dark:text-white">{{ item.title }}</h2>
                <p class="mt-1.5 text-sm text-neutral-500 dark:text-neutral-400">{{ item.body }}</p>
              </div>
            }
          </section>
        </main>
      }
    </div>
  `,
})
export class HomeComponent {
  protected readonly currentUser = inject(CurrentUserService);
  protected readonly highlights = HIGHLIGHTS;
  private readonly router = inject(Router);

  /** ADMINISTRATEUR always lands in the admin shell; every other authenticated role lands in the student shell. */
  protected readonly destination = computed(() =>
    this.currentUser.highestRole() === 'ADMINISTRATEUR' ? '/admin' : '/student',
  );

  constructor() {
    effect(() => {
      if (this.currentUser.authenticated()) {
        this.router.navigateByUrl(this.destination());
      }
    });
  }
}
