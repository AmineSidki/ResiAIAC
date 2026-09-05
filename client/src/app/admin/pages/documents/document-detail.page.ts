import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { DocumentService } from '../../../core/services/document.service';
import { OwnerNameService } from '../../shared/owner-name/owner-name.service';
import { DocumentDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';

/**
 * Review-only: no "new document" action exists anywhere on this route
 * (DocumentController.save was removed server-side). validate()/invalidate()
 * are the only writes this page performs, both via the single MANAGER-gated
 * PUT that sets etat + noteSurValidite together — there's no separate
 * endpoint for the note alone.
 */
@Component({
  selector: 'app-document-detail-page',
  standalone: true,
  imports: [RouterLink, FormsModule, StatusBadgeComponent, ButtonComponent, InputComponent, SkeletonRowsComponent],
  template: `
    <a routerLink="/admin/documents" class="text-sm text-primary-600 hover:text-primary-700 dark:text-primary-400">&larr; Retour aux documents</a>

    @if (loading()) {
      <div class="mt-4"><app-skeleton-rows [rows]="3" [columns]="2"></app-skeleton-rows></div>
    } @else {
      @if (document(); as d) {
        <div class="mt-4 grid grid-cols-1 gap-4 lg:grid-cols-[1.2fr_1fr]">
          <div class="rounded-lg border border-neutral-200 bg-white p-4 dark:border-white/10 dark:bg-white/5">
            @if (previewUrl(); as url) {
              @if (isImage(d.nomFichier)) {
                <img [src]="url" alt="Aperçu du document" class="max-h-[600px] w-full rounded-md object-contain" />
              } @else {
                <iframe [src]="safePreviewUrl()" class="h-[600px] w-full rounded-md border border-neutral-100 dark:border-white/10" title="Aperçu du document"></iframe>
              }
            } @else {
              <div class="flex h-64 items-center justify-center text-sm text-neutral-400">Chargement de l'aperçu…</div>
            }
          </div>

          <div class="rounded-lg border border-neutral-200 bg-white p-6 dark:border-white/10 dark:bg-white/5">
            <div class="flex items-start justify-between">
              <div>
                <h1 class="text-base font-semibold text-neutral-900 break-all dark:text-white">{{ d.nomFichier }}</h1>
                <p class="mt-1 text-xs text-neutral-500 dark:text-neutral-400">Propriétaire {{ ownerName() ?? '…' }}</p>
              </div>
              <app-status-badge kind="document" [value]="d.etat ?? 'AUCUN'"></app-status-badge>
            </div>

            <div class="mt-4">
              <app-input label="Note sur la validité" [(ngModel)]="note" type="text" [ngModelOptions]="{ standalone: true }"></app-input>
            </div>

            <div class="mt-4 flex gap-2">
              <app-button [loading]="saving()" (click)="setEtat('VALIDE')">Valider</app-button>
              <app-button variant="danger" [loading]="saving()" (click)="setEtat('INVALIDE')">Invalider</app-button>
            </div>
          </div>
        </div>
      }
    }
  `,
})
export class DocumentDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly documentService = inject(DocumentService);
  private readonly ownerNameService = inject(OwnerNameService);
  private readonly toast = inject(ToastService);
  private readonly sanitizer = inject(DomSanitizer);

  private documentId!: string;

  protected readonly document = signal<DocumentDto | null>(null);
  protected readonly ownerName = signal<string | null>(null);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly previewUrl = signal<string | null>(null);
  /** iframe src requires an explicitly trusted SafeResourceUrl — plain string binding is blocked by Angular's sanitizer regardless of scheme. */
  protected readonly safePreviewUrl = computed<SafeResourceUrl | null>(() => {
    const url = this.previewUrl();
    return url ? this.sanitizer.bypassSecurityTrustResourceUrl(url) : null;
  });
  protected note = '';

  protected readonly isImage = (name: string) => /\.(png|jpe?g|gif|webp)$/i.test(name);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.documentId = id;

    this.documentService.getById(id).subscribe({
      next: (d) => {
        this.document.set(d);
        this.note = d.noteSurValidite ?? '';
        this.loading.set(false);
        this.ownerNameService.resolveOne(d.proprietaire).subscribe((name) => this.ownerName.set(name));
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });

    this.documentService.getUrlById(id).subscribe({
      next: (url) => this.previewUrl.set(url),
      error: (err: AppError) => this.toast.showError(err.message),
    });
  }

  protected setEtat(etat: 'VALIDE' | 'INVALIDE'): void {
    const current = this.document();
    if (!current) return;
    this.saving.set(true);
    this.documentService
      .update({
        id: this.documentId,
        dto: { ...current, etat, noteSurValidite: this.note || null },
      })
      .subscribe({
        next: (updated) => {
          this.saving.set(false);
          this.document.set(updated);
          this.toast.show(etat === 'VALIDE' ? 'Document validé.' : 'Document invalidé.', 'success');
        },
        error: (err: AppError) => {
          this.saving.set(false);
          this.toast.showError(err.message);
        },
      });
  }
}
