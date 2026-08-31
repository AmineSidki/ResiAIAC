import { Component, OnInit, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../../../core/services/document.service';
import { DocumentDto } from '../../../core/models/dtos';
import { ETAT_DOCUMENT_VALUES, EtatDocument } from '../../../core/models/enums';
import { AppError } from '../../../core/api/app-error';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { DataTableComponent, DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SkeletonRowsComponent } from '../../shared/skeleton/skeleton-rows.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

/**
 * This screen is review-only by design: DocumentController.save was removed
 * server-side, so there is intentionally no "new document" action anywhere
 * here — documents only ever arrive via the student self-service upload
 * routes. getAll/getAllByStatus are RESPONSABLE-gated (confirmed against
 * DocumentController.java, and reflected in admin.routes.ts's route guard —
 * a plain MANAGER never reaches this page).
 */
@Component({
  selector: 'app-document-list-page',
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
      <div>
        <h1 class="text-lg font-semibold text-neutral-900">Documents</h1>
        <p class="text-sm text-neutral-500">File d'attente de validation — lecture seule, aucune création ici.</p>
      </div>
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
        <app-skeleton-rows [columns]="3"></app-skeleton-rows>
      } @else if (rows().length === 0) {
        <app-empty-state [reason]="etatFilter() ? 'filtered' : 'no-data'"></app-empty-state>
      } @else {
        <app-data-table [columns]="columns()" [rows]="rows()" [trackBy]="rowId"></app-data-table>
        <app-pagination [currentPage]="page()" [totalPages]="totalPages()" [totalElements]="totalElements()" (pageChange)="goToPage($event)"></app-pagination>
      }
    </div>

    <ng-template #nameTpl let-row>
      <a [routerLink]="['/admin/documents', row.id]" class="font-medium text-primary-600 hover:text-primary-700">
        {{ row.nomFichier }}
      </a>
    </ng-template>

    <ng-template #etatTpl let-row>
      <app-status-badge kind="document" [value]="row.etat ?? 'AUCUN'"></app-status-badge>
    </ng-template>
  `,
})
export class DocumentListPageComponent implements OnInit {
  private readonly documentService = inject(DocumentService);
  private readonly toast = inject(ToastService);

  protected readonly rows = signal<DocumentDto[]>([]);
  protected readonly loading = signal(true);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);
  protected readonly etatFilter = signal<EtatDocument | null>(null);

  protected readonly etatOptions: SelectOption[] = ETAT_DOCUMENT_VALUES.map((v) => ({ value: v, label: v }));
  protected readonly rowId = (row: DocumentDto) => row.id;

  private readonly nameTpl = viewChild<TemplateRef<{ $implicit: DocumentDto }>>('nameTpl');
  private readonly etatTpl = viewChild<TemplateRef<{ $implicit: DocumentDto }>>('etatTpl');

  protected readonly columns = computed<DataTableColumn<DocumentDto>[]>(() => [
    { key: 'nomFichier', header: 'Fichier', cellTemplate: this.nameTpl() },
    { key: 'proprietaire', header: 'Propriétaire', accessor: (r) => r.proprietaire },
    { key: 'etat', header: 'État', cellTemplate: this.etatTpl() },
  ]);

  ngOnInit(): void {
    this.load();
  }

  protected onFilterChange(value: EtatDocument | ''): void {
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
      ? this.documentService.getAllByStatus(etat, { page: this.page() })
      : this.documentService.getAll({ page: this.page() });

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
