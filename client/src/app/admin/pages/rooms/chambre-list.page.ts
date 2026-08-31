import { Component, TemplateRef, computed, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EntityCrudTableComponent, EntityFieldConfig } from '../../shared/entity-crud-table/entity-crud-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { ChambreService } from '../../../core/services/chambre.service';
import { EtageService } from '../../../core/services/etage.service';
import { ChambreDto, ChambreUpdateRequest } from '../../../core/models/dtos';
import { ETAT_CHAMBRE_VALUES } from '../../../core/models/enums';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';

/** Chambre: RESPONSABLE-gated writes, GET / and GET /{id} open to any authenticated user (confirmed against ChambreController.java). */
@Component({
  selector: 'app-chambre-list-page',
  standalone: true,
  imports: [RouterLink, EntityCrudTableComponent, StatusBadgeComponent],
  template: `
    <app-entity-crud-table
      [service]="service"
      [columns]="columns()"
      [fields]="fields()"
      title="Chambres"
      entityLabel="une chambre"
      [canWrite]="canWrite()"
      [emptyDto]="emptyDto"
      [buildUpdateRequest]="buildUpdateRequest"
    ></app-entity-crud-table>

    <ng-template #etatTpl let-row>
      <app-status-badge kind="chambre" [value]="row.etat ?? 'LIBRE'"></app-status-badge>
    </ng-template>

    <ng-template #matriculeTpl let-row>
      <a [routerLink]="['/admin/chambres', row.id]" class="font-medium text-primary-600 hover:text-primary-700">
        {{ row.matricule }}
      </a>
    </ng-template>
  `,
})
export class ChambreListPageComponent {
  protected readonly service = inject(ChambreService);
  private readonly etageService = inject(EtageService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  private readonly etageNameById = signal<Map<string, string>>(new Map());
  private readonly etatTpl = viewChild<TemplateRef<{ $implicit: ChambreDto }>>('etatTpl');
  private readonly matriculeTpl = viewChild<TemplateRef<{ $implicit: ChambreDto }>>('matriculeTpl');

  protected readonly columns = computed<DataTableColumn<ChambreDto>[]>(() => {
    const names = this.etageNameById();
    const cols: DataTableColumn<ChambreDto>[] = [
      { key: 'matricule', header: 'Matricule', cellTemplate: this.matriculeTpl(), accessor: (r) => r.matricule },
      { key: 'etage', header: 'Étage', accessor: (r) => names.get(r.etage) ?? r.etage },
      { key: 'capacite', header: 'Capacité', accessor: (r) => String(r.capacite) },
      { key: 'etat', header: 'État', cellTemplate: this.etatTpl() },
    ];
    return cols;
  });

  protected readonly fields = computed<EntityFieldConfig<ChambreDto>[]>(() => [
    { key: 'matricule', label: 'Matricule', required: true },
    { key: 'capacite', label: 'Capacité', type: 'number', required: true, min: 1 },
    {
      key: 'etage',
      label: 'Étage',
      type: 'select',
      required: true,
      options: Array.from(this.etageNameById(), ([value, label]) => ({ value, label })),
    },
    {
      key: 'etat',
      label: 'État',
      type: 'select',
      required: true,
      options: ETAT_CHAMBRE_VALUES.map((v) => ({ value: v, label: v })),
    },
  ]);

  protected readonly emptyDto = (): ChambreDto => ({
    id: null,
    matricule: '',
    capacite: 1,
    etat: 'LIBRE',
    reservations: [],
    reclamations: [],
    combinaisonsUpc: [],
    etage: '',
  });

  protected readonly buildUpdateRequest = (id: string, dto: ChambreDto): ChambreUpdateRequest => ({ id, dto });

  constructor() {
    this.etageService.getAll().subscribe((etages) => {
      this.etageNameById.set(new Map(etages.map((e) => [e.id as string, `Étage ${e.numero}`])));
    });
  }
}
