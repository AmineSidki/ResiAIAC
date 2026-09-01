import { Component, inject, signal } from '@angular/core';
import { ChambreManualService } from '../../../core/services/chambre-manual.service';
import { ChambreDto } from '../../../core/models/dtos';

/** Diagnostic twin of etage-manual.page.ts, for Chambre. See that file for why this exists. */
@Component({
  selector: 'app-chambre-manual-page',
  standalone: true,
  template: `
    <div style="padding: 16px; font-family: monospace;">
      <h2>Chambre — manual diagnostic</h2>

      @if (loading()) {
        <p>Loading…</p>
      }

      @if (error()) {
        <pre style="color: #f66; white-space: pre-wrap;">ERROR: {{ error() }}</pre>
      }

      @if (rows(); as chambres) {
        <p>Got {{ chambres.length }} row(s).</p>
        <table border="1" cellpadding="6" style="border-collapse: collapse;">
          <thead>
            <tr>
              <th>id</th>
              <th>matricule</th>
              <th>capacite</th>
              <th>etat</th>
              <th>etage</th>
            </tr>
          </thead>
          <tbody>
            @for (c of chambres; track c.id) {
              <tr>
                <td>{{ c.id }}</td>
                <td>{{ c.matricule }}</td>
                <td>{{ c.capacite }}</td>
                <td>{{ c.etat }}</td>
                <td>{{ c.etage }}</td>
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
export class ChambreManualPageComponent {
  private readonly chambreService = inject(ChambreManualService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly rows = signal<ChambreDto[] | null>(null);
  protected readonly raw = signal<string>('');

  constructor() {
    this.chambreService.getAll().subscribe({
      next: (chambres) => {
        this.raw.set(JSON.stringify(chambres, null, 2));
        this.rows.set(chambres);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err instanceof Error ? err.message : JSON.stringify(err, null, 2));
        this.loading.set(false);
      },
    });
  }
}
