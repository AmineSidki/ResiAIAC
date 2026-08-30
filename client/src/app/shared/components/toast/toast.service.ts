import { Injectable, signal } from '@angular/core';

export type ToastTone = 'info' | 'success' | 'danger';

export interface Toast {
  id: number;
  message: string;
  tone: ToastTone;
}

let nextId = 0;

@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly toastsSignal = signal<Toast[]>([]);
  readonly toasts = this.toastsSignal.asReadonly();

  show(message: string, tone: ToastTone = 'info', durationMs = 4000): void {
    const toast: Toast = { id: nextId++, message, tone };
    this.toastsSignal.update((current) => [...current, toast]);
    setTimeout(() => this.dismiss(toast.id), durationMs);
  }

  /** Convenience wired to AppError.presentation — see core/api/app-error.ts. */
  showError(message: string): void {
    this.show(message, 'danger');
  }

  dismiss(id: number): void {
    this.toastsSignal.update((current) => current.filter((toast) => toast.id !== id));
  }
}
