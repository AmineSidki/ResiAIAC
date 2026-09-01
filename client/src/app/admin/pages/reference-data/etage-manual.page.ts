import { Component, inject, signal } from '@angular/core';
import { EtageManualService } from '../../../core/services/etage-manual.service';
import { EtageDto } from '../../../core/models/dtos';

/**
 * Temporary diagnostic page: same data as /admin/reference/etages, but
 * fetched via EtageManualService (no BaseCrudService) and rendered with a
 * bare template (no EntityCrudTableComponent). If the list renders fine
 * here, both of those are cleared and the bug is elsewhere (DTO shape,
 * interceptor, backend). If it still breaks, note the exact error shown
 * on-screen below (nothing is swallowed here — success, error, and raw
 * payload are all rendered directly).
 *
 * Delete this file (and its route in admin.routes.ts) once done testing.
 */
@Component({
  selector: 'app-etage-manual-page',
  standalone: true,
  template: `
    <div style="padding: 16px; font-family: monospace;">
      <h2>Etage — manual diagnostic</h2>

      @if (loading()) {
        <p>Loading…</p>
      }

      @if (error()) {
        <pre style="color: #f66; white-space: pre-wrap;">ERROR: {{ error() }}</pre>
      }

      @if (rows(); as etages) {
        <p>Got {{ etages.length }} row(s).</p>
        <table border="1" cellpadding="6" style="border-collapse: collapse;">
          <thead>
            <tr>
              <th>id</th>
              <th>numero</th>
              <th>batiment</th>
              <th>chambres.length</th>
            </tr>
          </thead>
          <tbody>
            @for (e of etages; track e.id) {
              <tr>
                <td>{{ e.id }}</td>
                <td>{{ e.numero }}</td>
                <td>{{ e.batiment }}</td>
                <td>{{ e.chambres?.length }}</td>
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
export class EtageManualPageComponent {
  private readonly etageService = inject(EtageManualService);

  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly rows = signal<EtageDto[] | null>(null);
  protected readonly raw = signal<string>('');

  constructor() {
    this.etageService.getAll().subscribe({
      next: (etages) => {
        this.raw.set(JSON.stringify(etages, null, 2));
        this.rows.set(etages);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err instanceof Error ? err.message : JSON.stringify(err, null, 2));
        this.loading.set(false);
      },
    });
  }
}
