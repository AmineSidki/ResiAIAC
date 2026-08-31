import { Component, computed, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { ButtonComponent } from '../../../shared/components/button/button.component';

/**
 * A single "paste an id, verify it resolves to something real" field —
 * the fallback for foreign keys where the current role can GET-by-id but
 * can't list. Concretely: a plain MANAGER assigning a UtilisateurPromotionChambre
 * can look up a specific student by UUID (GET /utilisateur/{id} is
 * MANAGER-accessible) but can't browse a dropdown of all students (GET
 * /utilisateur/ is RESPONSABLE-only) — this is that lookup box, reused
 * anywhere the same shape of constraint shows up.
 */
@Component({
  selector: 'app-id-lookup-field',
  standalone: true,
  imports: [FormsModule, ButtonComponent],
  template: `
    <div class="flex flex-col gap-1">
      <label class="text-sm font-medium text-neutral-700">{{ label() }}</label>
      <div class="flex gap-2">
        <input
          [(ngModel)]="rawValue"
          [placeholder]="placeholder()"
          class="min-w-0 flex-1 rounded-md border border-neutral-300 px-3 py-2 text-sm shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
        <app-button variant="secondary" size="sm" [loading]="verifying()" (click)="verify()">Vérifier</app-button>
      </div>
      @if (state(); as s) {
        @if (s.kind === 'found') {
          <p class="text-xs text-success-500">&#10003; {{ s.label }}</p>
        } @else if (s.kind === 'not-found') {
          <p class="text-xs text-danger-500">Aucun résultat pour cet identifiant.</p>
        }
      }
    </div>
  `,
})
export class IdLookupFieldComponent {
  readonly label = input.required<string>();
  readonly placeholder = input('UUID');
  /** Given the raw id string, resolve it — null means "not found". Errors are treated as not-found. */
  readonly lookup = input.required<(id: string) => Observable<{ label: string } | null>>();
  readonly resolved = output<string | null>();

  protected rawValue = '';
  protected readonly verifying = signal(false);
  protected readonly state = signal<{ kind: 'found'; label: string } | { kind: 'not-found' } | null>(null);

  protected verify(): void {
    const id = this.rawValue.trim();
    if (!id) return;
    this.verifying.set(true);
    this.state.set(null);
    this.lookup()(id).subscribe({
      next: (result) => {
        this.verifying.set(false);
        if (result) {
          this.state.set({ kind: 'found', label: result.label });
          this.resolved.emit(id);
        } else {
          this.state.set({ kind: 'not-found' });
          this.resolved.emit(null);
        }
      },
      error: () => {
        this.verifying.set(false);
        this.state.set({ kind: 'not-found' });
        this.resolved.emit(null);
      },
    });
  }
}
