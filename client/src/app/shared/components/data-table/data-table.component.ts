import { CommonModule } from '@angular/common';
import { Component, TemplateRef, input } from '@angular/core';

export interface DataTableColumn<T> {
  key: string;
  header: string;
  /** Plain accessor for simple text cells. Skip this and use `cellTemplate` for anything richer (badges, actions). */
  accessor?: (row: T) => string;
  /** Row template for this column — receives the row as $implicit. Takes priority over `accessor`. */
  cellTemplate?: TemplateRef<{ $implicit: T }>;
}

/**
 * Generic table shell over any Page<T>.content array. Columns are declared
 * data, not markup — pass a cellTemplate for anything beyond plain text
 * (StatusBadge, row actions, links).
 */
@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="overflow-x-auto rounded-lg border border-neutral-200">
      <table class="min-w-full divide-y divide-neutral-200">
        <thead class="bg-neutral-50">
          <tr>
            @for (column of columns(); track column.key) {
              <th class="px-4 py-2.5 text-left text-xs font-semibold uppercase tracking-wide text-neutral-500">
                {{ column.header }}
              </th>
            }
          </tr>
        </thead>
        <tbody class="divide-y divide-neutral-100 bg-white">
          @for (row of rows(); track trackBy()(row)) {
            <tr class="hover:bg-neutral-50">
              @for (column of columns(); track column.key) {
                <td class="whitespace-nowrap px-4 py-2.5 text-sm text-neutral-700">
                  @if (column.cellTemplate) {
                    <ng-container *ngTemplateOutlet="column.cellTemplate; context: { $implicit: row }"></ng-container>
                  } @else {
                    {{ column.accessor ? column.accessor(row) : '' }}
                  }
                </td>
              }
            </tr>
          } @empty {
            <tr>
              <td [attr.colspan]="columns().length" class="px-4 py-6 text-center text-sm text-neutral-400">
                {{ emptyMessage() }}
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class DataTableComponent<T> {
  readonly columns = input.required<DataTableColumn<T>[]>();
  readonly rows = input.required<T[]>();
  readonly emptyMessage = input('No results.');
  readonly trackBy = input<(row: T) => unknown>((row: T) => row);
}
