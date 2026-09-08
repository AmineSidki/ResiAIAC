import { Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

const STORAGE_KEY = 'resiaiac.theme';

/**
 * Dark mode toggle, persisted in localStorage (survives reload/new tab —
 * "persisted in local state" per the brief). Tailwind is configured with
 * `darkMode: 'class'` (see tailwind.config.js), so this service's only job
 * is keeping the `dark` class on <html> in sync with the signal, and
 * initializing from whatever the user chose last time (falling back to the
 * OS-level `prefers-color-scheme` on first visit).
 *
 * index.html carries a small inline script that applies the same class
 * before Angular bootstraps, so there's no flash of the wrong theme on load.
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly themeSignal = signal<Theme>(this.readInitialTheme());
  readonly theme = this.themeSignal.asReadonly();

  constructor() {
    this.apply(this.themeSignal());
  }

  toggle(): void {
    this.set(this.themeSignal() === 'dark' ? 'light' : 'dark');
  }

  set(theme: Theme): void {
    this.themeSignal.set(theme);
    this.apply(theme);
    try {
      localStorage.setItem(STORAGE_KEY, theme);
    } catch {
      // Storage unavailable (private browsing) — theme still applies for this session.
    }
  }

  private apply(theme: Theme): void {
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }

  private readInitialTheme(): Theme {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored === 'dark' || stored === 'light') return stored;
    } catch {
      // ignore — fall through to system preference
    }
    return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
}
