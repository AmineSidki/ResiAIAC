import { Component, inject, signal } from '@angular/core';
import { BatimentManualService } from '../../../core/services/batiment-manual.service';
import { BatimentDto } from '../../../core/models/dtos';

/** Diagnostic twin of etage-manual.page.ts, for Batiment. See that file for why this exists. */
@Component({
  selector: 'app-batiment-manual-page',
  standalone: true,
  template: `
    <div style="padding: 16px; font-family: monospace;">
      <h2>Batiment — manual diagnostic</h2>

      @if (loading()) {
        <p>Loading…</p>
      }

      @if (error()) {
        <pre style="color: #f66; white-space: pre-wrap;">ERROR: {{ error() }}</pre>
      }

      @if (rows(); as batiments) {
        <p>Got {{ batiments.length }} row(s).</p>
        <table border="1" cellpadding="6" style="border-collapse: collapse;">
          <thead>
            <tr>
              <th>id</th>
              <th>nom</th>
              <th>etages.length</th>
            </tr>
          </thead>
          <tbody>
            @for (b of batiments; track b.id) {
              <tr>
                <td>{{ b.id }}</td>
                <td>{{ b.nom }}</td>
                <td>{{ b.etages?.length }}</td>
              </tr>
            }
          </tbody>
        </table>

        <h3>Raw payload</h3>
        <pre style="white-space: pre-wrap;">{{ raw() }}</pre>
      }
    </div>
  `,
})
export class BatimentManualPageComponent {
  private readonly batimentService = inject(BatimentManualService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly rows = signal<BatimentDto[] | null>(null);
  protected readonly raw = signal<string>('');

  constructor() {
    this.batimentService.getAll().subscribe({
      next: (batiments) => {
        this.raw.set(JSON.stringify(batiments, null, 2));
        this.rows.set(batiments);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err instanceof Error ? err.message : JSON.stringify(err, null, 2));
        this.loading.set(false);
      },
    });
  }
}
