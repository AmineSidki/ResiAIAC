import { Component, computed, input, output } from '@angular/core';

/**
 * Drives page number against a Spring Page<T> response — takes the raw
 * `number` (0-based current page) and `totalPages` fields directly, no
 * translation needed at the call site.
 */
@Component({
  selector: 'app-pagination',
  standalone: true,
  template: `
    @if (totalPages() > 1) {
      <nav class="flex items-center justify-between border-t border-neutral-200 px-2 py-3" aria-label="Pagination">
        <p class="text-sm text-neutral-500">
          Page {{ currentPage() + 1 }} of {{ totalPages() }}
          @if (totalElements() !== null) {
            &middot; {{ totalElements() }} total
          }
        </p>
        <div class="flex gap-2">
          <button
            type="button"
            [disabled]="currentPage() === 0"
            (click)="pageChange.emit(currentPage() - 1)"
            class="rounded-md border border-neutral-300 px-3 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Previous
          </button>
          <button
            type="button"
            [disabled]="currentPage() >= totalPages() - 1"
            (click)="pageChange.emit(currentPage() + 1)"
            class="rounded-md border border-neutral-300 px-3 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </nav>
    }
  `,
})
export class PaginationComponent {
  readonly currentPage = input.required<number>();
  readonly totalPages = input.required<number>();
  readonly totalElements = input<number | null>(null);
  readonly pageChange = output<number>();
}
