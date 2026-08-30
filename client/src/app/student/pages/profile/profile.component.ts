import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { UtilisateurDto } from '../../../core/models/dtos';
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

@Component({
  selector: 'app-student-profile',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe, InputComponent, ButtonComponent, SkeletonComponent],
  template: `
    <div class="flex flex-col gap-4">
      <h1 class="text-lg font-semibold text-neutral-900">Mon profil</h1>

      @if (loading()) {
        <div class="flex flex-col gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm">
          <app-skeleton variant="text" width="60%" />
          <app-skeleton variant="text" width="40%" />
          <app-skeleton variant="block" height="2.5rem" />
          <app-skeleton variant="block" height="2.5rem" />
        </div>
      } @else if (profile()) {
        @let user = profile()!;
        <!-- Read-only identity block — everything on UtilisateurDto besides adresse/telephone is admin-managed. -->
        <div class="grid grid-cols-2 gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm">
          <div>
            <p class="text-xs text-neutral-500">Nom</p>
            <p class="text-sm font-medium text-neutral-900">{{ user.nom }}</p>
          </div>
          <div>
            <p class="text-xs text-neutral-500">Prénom</p>
            <p class="text-sm font-medium text-neutral-900">{{ user.prenom }}</p>
          </div>
          <div>
            <p class="text-xs text-neutral-500">Email</p>
            <p class="truncate text-sm font-medium text-neutral-900">{{ user.email }}</p>
          </div>
          <div>
            <p class="text-xs text-neutral-500">CIN</p>
            <p class="text-sm font-medium text-neutral-900">{{ user.cin }}</p>
          </div>
          @if (user.createdAt) {
            <div class="col-span-2">
              <p class="text-xs text-neutral-500">Membre depuis</p>
              <p class="text-sm text-neutral-700">{{ user.createdAt | date: 'longDate' }}</p>
            </div>
          }
        </div>

        <!-- Editable block — only adresse/telephone are writable per UpdateMeRequest. -->
        <form
          [formGroup]="form"
          (ngSubmit)="onSubmit()"
          class="flex flex-col gap-3 rounded-lg border border-neutral-100 bg-surface p-4 shadow-sm"
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
  private readonly toastService = inject(ToastService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly profile = signal<UtilisateurDto | null>(null);

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
}
