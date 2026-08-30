import { Component, OnInit, inject, signal } from '@angular/core';
import { DocumentService } from '../../../core/services/document.service';
import { DocumentDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';

type SlotKey = 'pfp' | 'cin' | 'dip';

interface Slot {
  key: SlotKey;
  label: string;
  hint: string;
  /**
   * DocumentDto.nomSceau on the wire is literally FileType.bucketName
   * ("images"/"cins"/"diplomes") — see DocumentServiceImpl.uploadMyDocument,
   * which sets nomSceau = fileType.getBucketName(). This is the only signal
   * the response gives for which fixed slot a DocumentDto belongs to.
   * Flagged gap: core/models/enums.ts's FileType comment claims
   * "bucketName is server-internal and never appears in DTO JSON," which
   * this contradicts — nomSceau *is* the bucket name, in the JSON, every
   * time. Not fixing that comment here (it's a shared Phase 0 file) but
   * noting it since the next person mapping nomSceau will hit the same
   * confusion.
   */
  bucketName: string;
  doc: DocumentDto | null;
  uploading: boolean;
}

const INITIAL_SLOTS: Slot[] = [
  { key: 'pfp', label: 'Photo de profil', hint: 'JPG ou PNG', bucketName: 'images', doc: null, uploading: false },
  { key: 'cin', label: 'CIN', hint: 'Recto, lisible', bucketName: 'cins', doc: null, uploading: false },
  { key: 'dip', label: 'Diplôme', hint: 'Dernier diplôme obtenu', bucketName: 'diplomes', doc: null, uploading: false },
];

@Component({
  selector: 'app-student-documents',
  standalone: true,
  imports: [ButtonComponent, StatusBadgeComponent, SkeletonComponent],
  template: `
    <div class="flex flex-col gap-4">
      <h1 class="text-lg font-semibold text-neutral-900">Mes documents</h1>
      <p class="text-sm text-neutral-500">
        Chaque envoi remplace le document précédent du même type — il n'y a pas d'historique.
      </p>

      @if (loading()) {
        <div class="flex flex-col gap-3">
          @for (i of [0, 1, 2]; track i) {
            <app-skeleton variant="block" height="5rem" />
          }
        </div>
      } @else {
        <div class="flex flex-col gap-3">
          @for (slot of slots(); track slot.key) {
            <div class="flex items-center justify-between gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm">
              <div class="min-w-0">
                <p class="text-sm font-medium text-neutral-900">{{ slot.label }}</p>
                <p class="text-xs text-neutral-500">{{ slot.hint }}</p>
                <div class="mt-1.5">
                  @if (slot.doc) {
                    <app-status-badge kind="document" [value]="slot.doc.etat ?? 'AUCUN'" />
                  } @else {
                    <app-status-badge kind="document" value="AUCUN" />
                  }
                </div>
              </div>
              <div class="flex shrink-0 flex-col items-end gap-1.5">
                @if (slot.doc) {
                  <button
                    type="button"
                    (click)="previewSlot(slot)"
                    class="text-xs font-medium text-primary-600 hover:underline"
                  >
                    Voir
                  </button>
                }
                <app-button size="sm" variant="secondary" [loading]="slot.uploading" (click)="fileInput.click()">
                  {{ slot.doc ? 'Remplacer' : 'Envoyer' }}
                </app-button>
                <input
                  #fileInput
                  type="file"
                  accept="image/*,.pdf"
                  class="hidden"
                  (change)="onFileSelected(slot, $event)"
                />
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class DocumentsComponent implements OnInit {
  private readonly documentService = inject(DocumentService);
  private readonly toastService = inject(ToastService);

  protected readonly loading = signal(true);
  protected readonly slots = signal<Slot[]>(INITIAL_SLOTS);

  ngOnInit(): void {
    this.refresh();
  }

  private refresh(): void {
    this.loading.set(true);
    this.documentService.getAllMy().subscribe({
      next: (page) => {
        this.slots.update((current) =>
          current.map((slot) => ({
            ...slot,
            doc: page.content.find((d) => d.nomSceau === slot.bucketName) ?? null,
            uploading: false,
          })),
        );
        this.loading.set(false);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toastService.showError(err.message);
      },
    });
  }

  protected onFileSelected(slot: Slot, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = ''; // allow re-selecting the same file next time
    if (!file) return;

    this.setUploading(slot.key, true);
    this.uploadFor(slot.key, file).subscribe({
      next: () => {
        this.toastService.show(`${slot.label} envoyé.`, 'success');
        this.refresh();
      },
      error: (err: AppError) => {
        this.setUploading(slot.key, false);
        this.toastService.showError(err.message);
      },
    });
  }

  protected previewSlot(slot: Slot): void {
    if (!slot.doc?.id) return;
    this.documentService.getMyUrlById(slot.doc.id).subscribe({
      next: (url) => window.open(url, '_blank', 'noopener'),
      error: (err: AppError) => this.toastService.showError(err.message),
    });
  }

  private uploadFor(key: SlotKey, file: File) {
    switch (key) {
      case 'pfp':
        return this.documentService.uploadProfileImage(file);
      case 'cin':
        return this.documentService.uploadCin(file);
      case 'dip':
        return this.documentService.uploadDiploma(file);
    }
  }

  private setUploading(key: SlotKey, uploading: boolean): void {
    this.slots.update((current) => current.map((slot) => (slot.key === key ? { ...slot, uploading } : slot)));
  }
}
