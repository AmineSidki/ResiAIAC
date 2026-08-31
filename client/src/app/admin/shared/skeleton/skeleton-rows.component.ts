import { Component, input } from '@angular/core';

/**
 * Animated placeholder rows shown while a table's first page load is in
 * flight. Deliberately distinct from EmptyStateComponent — this means
 * "we don't know yet", not "we checked and there's nothing".
 */
@Component({
  selector: 'app-skeleton-rows',
  standalone: true,
  template: `
    <div class="overflow-hidden rounded-lg border border-neutral-200">
      <table class="min-w-full divide-y divide-neutral-200">
        <tbody class="divide-y divide-neutral-100 bg-white">
          @for (row of rowIndexes(); track row) {
            <tr>
              @for (col of colIndexes(); track col) {
                <td class="px-4 py-3">
                  <div class="h-3.5 animate-pulse rounded bg-neutral-100" [style.width.%]="widthFor(col)"></div>
                </td>
              }
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class SkeletonRowsComponent {
  readonly rows = input(5);
  readonly columns = input(4);

  protected get rowIndexes(): () => number[] {
    return () => Array.from({ length: this.rows() }, (_, i) => i);
  }

  protected get colIndexes(): () => number[] {
    return () => Array.from({ length: this.columns() }, (_, i) => i);
  }

  protected widthFor(col: number): number {
    // Vary widths so the skeleton doesn't look like a rigid grid.
    const pattern = [70, 45, 60, 35, 55, 40];
    return pattern[col % pattern.length];
  }
}
