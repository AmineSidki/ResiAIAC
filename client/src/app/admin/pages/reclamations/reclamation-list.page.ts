import { Component, OnInit, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReclamationService } from '../../../core/services/reclamation.service';
import { ReclamationDto } from '../../../core/models/dtos';
import { ETAT_RECLAMATION_VALUES, EtatReclamation } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

/** Every route on ReclamationController's admin side is MANAGER-gated (confirmed against ReclamationController.java). */
@Component({
  selector: 'app-reclamation-list-page',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    StatusBadgeComponent,
    SelectComponent,
    DataTableComponent,
    PaginationComponent,
    SkeletonRowsComponent,
    EmptyStateComponent,
  ],
  template: `
    <div class="flex items-center justify-between">
      <h1 class="text-lg font-semibold text-neutral-900">Réclamations</h1>
      <div class="w-56">
        <app-select
          placeholder="Tous les états"
          [options]="etatOptions"
          [ngModel]="etatFilter()"
          (ngModelChange)="onFilterChange($event)"
        ></app-select>
      </div>
    </div>

    <div class="mt-4">
      @if (loading()) {
        <app-skeleton-rows [columns]="4"></app-skeleton-rows>
      } @else if (rows().length === 0) {
        <app-empty-state [reason]="etatFilter() ? 'filtered' : 'no-data'"></app-empty-state>
      } @else {
        <app-data-table [columns]="columns()" [rows]="rows()" [trackBy]="rowId"></app-data-table>
        <app-pagination [currentPage]="page()" [totalPages]="totalPages()" [totalElements]="totalElements()" (pageChange)="goToPage($event)"></app-pagination>
      }
    </div>

    <ng-template #idTpl let-row>
      <a [routerLink]="['/admin/reclamations', row.id]" class="font-medium text-primary-600 hover:text-primary-700">
        {{ row.id?.slice(0, 8) }}…
      </a>
    </ng-template>

    <ng-template #etatTpl let-row>
      <app-status-badge kind="reclamation" [value]="row.etat ?? 'EN_ATTENTE'"></app-status-badge>
    </ng-template>
  `,
})
export class ReclamationListPageComponent implements OnInit {
  private readonly reclamationService = inject(ReclamationService);
  private readonly toast = inject(ToastService);

  protected readonly rows = signal<ReclamationDto[]>([]);
  protected readonly loading = signal(true);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);
  protected readonly etatFilter = signal<EtatReclamation | null>(null);

  protected readonly etatOptions: SelectOption[] = ETAT_RECLAMATION_VALUES.map((v) => ({ value: v, label: v }));

  protected readonly rowId = (row: ReclamationDto) => row.id;

  private readonly idTpl = viewChild<TemplateRef<{ $implicit: ReclamationDto }>>('idTpl');
  private readonly etatTpl = viewChild<TemplateRef<{ $implicit: ReclamationDto }>>('etatTpl');

  protected readonly columns = computed<DataTableColumn<ReclamationDto>[]>(() => [
    { key: 'id', header: 'ID', cellTemplate: this.idTpl() },
    { key: 'service', header: 'Service', accessor: (r) => String(r.service) },
    { key: 'message', header: 'Message', accessor: (r) => r.message ?? '—' },
    { key: 'etat', header: 'État', cellTemplate: this.etatTpl() },
  ]);

  ngOnInit(): void {
    this.load();
  }

  protected onFilterChange(value: EtatReclamation | ''): void {
    this.etatFilter.set(value || null);
    this.page.set(0);
    this.load();
  }

  protected goToPage(page: number): void {
    this.page.set(page);
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    const etat = this.etatFilter();
    const request$ = etat
      ? this.reclamationService.getAllByStatus(etat, { page: this.page() })
      : this.reclamationService.getAll({ page: this.page() });

    request$.subscribe({
      next: (result) => {
        this.rows.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });
  }
}
