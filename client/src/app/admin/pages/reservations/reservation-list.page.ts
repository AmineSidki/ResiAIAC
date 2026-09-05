import { Component, OnInit, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReservationService } from '../../../core/services/reservation.service';
import { OwnerNameService } from '../../shared/owner-name/owner-name.service';
import { ReservationDto } from '../../../core/models/dtos';
import { ETAT_RESERVATION_VALUES, EtatReservation } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

/**
 * ReservationController's admin-side routes are all MANAGER-gated
 * (confirmed against ReservationController.java). Unlike Document/Reclamation,
 * there is no dedicated `/by-etat/{etat}` endpoint — filtering happens
 * client-side over the current page, since the server only offers a plain
 * paginated getAll().
 */
@Component({
  selector: 'app-reservation-list-page',
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
      <h1 class="text-lg font-semibold text-neutral-900 dark:text-white">Réservations</h1>
      <div class="flex items-end gap-2">
        <div class="w-56">
          <app-select
            placeholder="Tous les états"
            [clearable]="true"
            [options]="etatOptions"
            [ngModel]="etatFilter()"
            (ngModelChange)="etatFilter.set($event || null)"
          ></app-select>
        </div>
        @if (etatFilter()) {
          <button
            type="button"
            (click)="etatFilter.set(null)"
            class="h-[38px] rounded-md border border-neutral-300 px-3 text-sm font-medium text-neutral-600 hover:bg-neutral-50 dark:border-white/10 dark:text-neutral-300 dark:hover:bg-white/5"
          >
            Réinitialiser
          </button>
        }
      </div>
    </div>

    <div class="mt-4">
      @if (loading()) {
        <app-skeleton-rows [columns]="3"></app-skeleton-rows>
      } @else if (filteredRows().length === 0) {
        <app-empty-state [reason]="etatFilter() ? 'filtered' : 'no-data'"></app-empty-state>
      } @else {
        <app-data-table [columns]="columns()" [rows]="filteredRows()" [trackBy]="rowId"></app-data-table>
        <app-pagination [currentPage]="page()" [totalPages]="totalPages()" [totalElements]="totalElements()" (pageChange)="goToPage($event)"></app-pagination>
      }
    </div>

    <ng-template #idTpl let-row>
      <a [routerLink]="['/admin/reservations', row.id]" class="font-medium text-primary-600 hover:text-primary-700">
        {{ row.id.slice(0, 8) }}…
      </a>
    </ng-template>

    <ng-template #ownerTpl let-row>
      {{ ownerNames().get(row.utilisateur) ?? '…' }}
    </ng-template>

    <ng-template #etatTpl let-row>
      <app-status-badge kind="reservation" [value]="row.etat ?? 'ACTIVE'"></app-status-badge>
    </ng-template>
  `,
})
export class ReservationListPageComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);
  private readonly ownerNameService = inject(OwnerNameService);
  private readonly toast = inject(ToastService);

  protected readonly rows = signal<ReservationDto[]>([]);
  protected readonly ownerNames = signal<Map<string, string>>(new Map());
  protected readonly loading = signal(true);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);
  protected readonly etatFilter = signal<EtatReservation | null>(null);

  protected readonly etatOptions: SelectOption[] = ETAT_RESERVATION_VALUES.map((v) => ({ value: v, label: v }));
  protected readonly rowId = (row: ReservationDto) => row.id;

  protected readonly filteredRows = computed(() => {
    const filter = this.etatFilter();
    const rows = this.rows();
    return filter ? rows.filter((r) => r.etat === filter) : rows;
  });

  private readonly idTpl = viewChild<TemplateRef<{ $implicit: ReservationDto }>>('idTpl');
  private readonly ownerTpl = viewChild<TemplateRef<{ $implicit: ReservationDto }>>('ownerTpl');
  private readonly etatTpl = viewChild<TemplateRef<{ $implicit: ReservationDto }>>('etatTpl');

  protected readonly columns = computed<DataTableColumn<ReservationDto>[]>(() => [
    { key: 'id', header: 'ID', cellTemplate: this.idTpl() },
    { key: 'utilisateur', header: 'Étudiant', cellTemplate: this.ownerTpl() },
    { key: 'chambre', header: 'Chambre', accessor: (r) => r.chambre },
    { key: 'etat', header: 'État', cellTemplate: this.etatTpl() },
  ]);

  ngOnInit(): void {
    this.load();
  }

  protected goToPage(page: number): void {
    this.page.set(page);
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.reservationService.getAll({ page: this.page() }).subscribe({
      next: (result) => {
        this.rows.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
        this.ownerNameService.resolveMany(result.content.map((r) => r.utilisateur)).subscribe((names) => this.ownerNames.set(names));
      },
      error: (err: AppError) => {
        this.loading.set(false);
        this.toast.showError(err.message);
      },
    });
  }
}
