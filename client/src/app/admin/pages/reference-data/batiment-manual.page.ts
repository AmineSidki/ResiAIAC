import { Component, inject, signal } from '@angular/core';
import { BatimentManualService } from '../../../core/services/batiment-manual.service';

/** Raw-JSON-only diagnostic twin of etage-manual.page.ts, for Batiment. */
@Component({
  selector: 'app-batiment-manual-page',
  standalone: true,
  template: `
    <div style="padding: 16px; font-family: monospace;">
      <h2>Batiment — manual diagnostic (raw JSON only)</h2>

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
export class BatimentManualPageComponent {
  private readonly batimentService = inject(BatimentManualService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly raw = signal<string>('');

  constructor() {
    this.batimentService.getAll().subscribe({
      next: (batiments) => {
        this.raw.set(JSON.stringify(batiments, null, 2));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err instanceof Error ? err.message : JSON.stringify(err, null, 2));
        this.loading.set(false);
      },
    });
  }
}
