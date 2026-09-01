import { Component, inject, signal } from '@angular/core';
import { ChambreManualService } from '../../../core/services/chambre-manual.service';

/** Raw-JSON-only diagnostic twin of etage-manual.page.ts, for Chambre. */
@Component({
  selector: 'app-chambre-manual-page',
  standalone: true,
  template: `
    <div style="padding: 16px; font-family: monospace;">
      <h2>Chambre — manual diagnostic (raw JSON only)</h2>

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
export class ChambreManualPageComponent {
  private readonly chambreService = inject(ChambreManualService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly raw = signal<string>('');

  constructor() {
    this.chambreService.getAll().subscribe({
      next: (chambres) => {
        this.raw.set(JSON.stringify(chambres, null, 2));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err instanceof Error ? err.message : JSON.stringify(err, null, 2));
        this.loading.set(false);
      },
    });
  }
}
