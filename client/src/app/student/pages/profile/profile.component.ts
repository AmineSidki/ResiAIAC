import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { DocumentService } from '../../../core/services/document.service';
import { UtilisateurDto, DocumentDto } from '../../../core/models/dtos';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { InputComponent } from '../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';

/** ^\+?[0-9]{8,15}$ — mirrors UpdateMeRequest.telephone exactly. */
const TELEPHONE_PATTERN = /^\+?[0-9]{8,15}$/;

interface EditForm {
  adresse: FormControl<string>;
  telephone: FormControl<string>;
}

/**
 * Profile pictures now live here rather than in the general document
 * upload list (Task A5): a photo is fundamentally identity/presentation,
 * not a validation-queue item like a CIN or diplôme scan, so it gets its
 * own avatar + upload control right next to the user's name instead of
 * sharing a slot list with those. `documents.component.ts` no longer has a
 * 'pfp' slot — see that file's comment for the corresponding removal.
 */
@Component({
  selector: 'app-student-profile',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe, InputComponent, ButtonComponent, SkeletonComponent],
  template: `
    <div class="flex flex-col gap-4">
      <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">Mon profil</h1>

      @if (loading()) {
        <div class="flex flex-col gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm dark:border-white/10 dark:bg-white/5">
          <app-skeleton variant="text" width="60%" />
          <app-skeleton variant="text" width="40%" />
          <app-skeleton variant="block" height="2.5rem" />
          <app-skeleton variant="block" height="2.5rem" />
        </div>
      } @else if (profile()) {
        @let user = profile()!;

        <!-- Avatar + name header, with the profile-picture upload control. -->
        <div class="flex items-center gap-4 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm dark:border-white/10 dark:bg-white/5">
          <div class="relative shrink-0">
            @if (avatarUrl()) {
              <img [src]="avatarUrl()" alt="Photo de profil" class="h-16 w-16 rounded-full object-cover ring-1 ring-neutral-200 dark:ring-white/10" />
            } @else {
              <div class="flex h-16 w-16 items-center justify-center rounded-full bg-primary-100 text-lg font-semibold text-primary-700 dark:bg-primary-900/40 dark:text-primary-300">
                {{ initials(user) }}
              </div>
            }
          </div>
          <div class="min-w-0 flex-1">
            <p class="truncate text-base font-semibold text-neutral-900 dark:text-white">{{ user.prenom }} {{ user.nom }}</p>
            <p class="truncate text-sm text-neutral-500 dark:text-neutral-400">{{ user.email }}</p>
            <button
              type="button"
              class="mt-1.5 text-xs font-medium text-primary-600 hover:underline dark:text-primary-400"
              [disabled]="avatarUploading()"
              (click)="avatarInput.click()"
            >
              {{ avatarUploading() ? 'Envoi…' : avatarUrl() ? 'Changer la photo' : 'Ajouter une photo' }}
            </button>
            <input
              #avatarInput
              type="file"
              accept="image/*"
              class="hidden"
              (change)="onAvatarSelected($event)"
            />
          </div>
        </div>

        <!-- Read-only identity block — everything on UtilisateurDto besides adresse/telephone is admin-managed. -->
        <div class="grid grid-cols-2 gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm dark:border-white/10 dark:bg-white/5">
          <div>
            <p class="text-xs text-neutral-500 dark:text-neutral-400">Nom</p>
            <p class="text-sm font-medium text-neutral-900 dark:text-white">{{ user.nom }}</p>
          </div>
          <div>
            <p class="text-xs text-neutral-500 dark:text-neutral-400">Prénom</p>
            <p class="text-sm font-medium text-neutral-900 dark:text-white">{{ user.prenom }}</p>
          </div>
          <div>
            <p class="text-xs text-neutral-500 dark:text-neutral-400">Email</p>
            <p class="truncate text-sm font-medium text-neutral-900 dark:text-white">{{ user.email }}</p>
          </div>
          <div>
            <p class="text-xs text-neutral-500 dark:text-neutral-400">CIN</p>
            <p class="text-sm font-medium text-neutral-900 dark:text-white">{{ user.cin }}</p>
          </div>
          @if (user.createdAt) {
            <div class="col-span-2">
              <p class="text-xs text-neutral-500 dark:text-neutral-400">Membre depuis</p>
              <p class="text-sm text-neutral-700 dark:text-neutral-300">{{ user.createdAt | date: 'longDate' }}</p>
            </div>
          }
        </div>

        <!-- Editable block — only adresse/telephone are writable per UpdateMeRequest. -->
        <form
          [formGroup]="form"
          (ngSubmit)="onSubmit()"
          class="flex flex-col gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm dark:border-white/10 dark:bg-white/5"
        >
          <app-input
            label="Adresse"
            formControlName="adresse"
            placeholder="Votre adresse"
          />
          <app-input
            label="Téléphone"
            type="tel"
            formControlName="telephone"
            placeholder="+212600000000"
            hint="8 à 15 chiffres, avec ou sans indicatif."
            [errorText]="telephoneErrorText()"
          />
          <app-button type="submit" [disabled]="form.invalid || !form.dirty" [loading]="saving()">
            Enregistrer
          </app-button>
        </form>
      }
    </div>
  `,
})
export class ProfileComponent implements OnInit {
  private readonly utilisateurService = inject(UtilisateurService);
  private readonly documentService = inject(DocumentService);
  private readonly toastService = inject(ToastService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly profile = signal<UtilisateurDto | null>(null);

  protected readonly avatarDoc = signal<DocumentDto | null>(null);
  protected readonly avatarUrl = signal<string | null>(null);
  protected readonly avatarUploading = signal(false);

  protected readonly form = new FormGroup<EditForm>({
    adresse: new FormControl('', { nonNullable: true }),
    telephone: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(TELEPHONE_PATTERN)],
    }),
  });

  ngOnInit(): void {
    this.utilisateurService.getMe().subscribe({
      next: (user) => {
        this.profile.set(user);
        this.form.reset({ adresse: user.adresse ?? '', telephone: user.telephone });
        this.loading.set(false);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toastService.showError(err.message);
      },
    });
    this.refreshAvatar();
  }

  protected initials(user: UtilisateurDto): string {
    return `${user.prenom.charAt(0)}${user.nom.charAt(0)}`.toUpperCase();
  }

  protected telephoneErrorText(): string | null {
    const control = this.form.controls.telephone;
    if (!control.touched || control.valid) return null;
    if (control.hasError('required')) return 'Le téléphone est obligatoire.';
    if (control.hasError('pattern')) return 'Numéro de téléphone invalide.';
    return null;
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { adresse, telephone } = this.form.getRawValue();
    this.saving.set(true);
    this.utilisateurService
      .updateMe({ adresse: adresse.trim() === '' ? null : adresse.trim(), telephone })
      .subscribe({
        next: (updated) => {
          this.saving.set(false);
          this.profile.set(updated);
          this.form.markAsPristine();
          this.toastService.show('Profil mis à jour.', 'success');
        },
        error: (err: AppError) => {
          this.saving.set(false);
          this.toastService.showError(err.message);
        },
      });
  }

  protected onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = ''; // allow re-selecting the same file next time
    if (!file) return;

    this.avatarUploading.set(true);
    this.documentService.uploadProfileImage(file).subscribe({
      next: () => {
        this.toastService.show('Photo de profil mise à jour.', 'success');
        this.refreshAvatar();
      },
      error: (err: AppError) => {
        this.avatarUploading.set(false);
        this.toastService.showError(err.message);
      },
    });
  }

  private refreshAvatar(): void {
    this.documentService.getMyProfileImage().subscribe({
      next: (doc) => {
        this.avatarDoc.set(doc);
        this.avatarUploading.set(false);
        if (!doc?.id) {
          this.avatarUrl.set(null);
          return;
        }
        this.documentService.getMyUrlById(doc.id).subscribe({
          next: (url) => this.avatarUrl.set(url),
          error: () => this.avatarUrl.set(null),
        });
      },
      error: () => this.avatarUploading.set(false),
    });
  }
}
