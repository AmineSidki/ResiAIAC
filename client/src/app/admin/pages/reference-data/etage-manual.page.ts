import { Component, inject, signal } from '@angular/core';
import { EtageManualService } from '../../../core/services/etage-manual.service';

/**
 * Temporary diagnostic page — no @for, no table, no iteration of any kind
 * over the response. Just loading/error state and the raw JSON text as a
 * string. If this still throws, the crash has nothing to do with rendering
 * a list at all.
 *
 * Delete this file (and its route in admin.routes.ts) once done testing.
 */
@Component({
  selector: 'app-etage-manual-page',
  standalone: true,
  template: `
    <div style="padding: 16px; font-family: monospace;">
      <h2>Etage — manual diagnostic (raw JSON only)</h2>

      @if (loading()) {
        <p>Loading…</p>
      }

      @if (error()) {
        <pre style="color: #f66; white-space: pre-wrap;">ERROR: {{ error() }}</pre>
      }

      @if (raw()) {
        <pre style="white-space: pre-wrap;">{{ raw() }}</pre>
      }
    </div>
  `,
})
export class EtageManualPageComponent {
  private readonly etageService = inject(EtageManualService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly raw = signal<string>('');

  constructor() {
    this.etageService.getAll().subscribe({
      next: (etages) => {
        this.raw.set(JSON.stringify(etages, null, 2));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err instanceof Error ? err.message : JSON.stringify(err, null, 2));
        this.loading.set(false);
      },
    });
  }
}
