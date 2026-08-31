import { Component, computed, inject } from '@angular/core';
import { EntityCrudTableComponent, EntityFieldConfig } from '../../shared/entity-crud-table/entity-crud-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { BatimentService } from '../../../core/services/batiment.service';
import { BatimentDto, BatimentUpdateRequest } from '../../../core/models/dtos';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';

/** Batiment: RESPONSABLE-gated writes, list open to any authenticated user (confirmed against BatimentController.java). */
@Component({
  selector: 'app-batiment-page',
  standalone: true,
  imports: [EntityCrudTableComponent],
  template: `
    <app-entity-crud-table
      [service]="service"
      [columns]="columns"
      [fields]="fields"
      title="Bâtiments"
      entityLabel="un bâtiment"
      [canWrite]="canWrite()"
      [emptyDto]="emptyDto"
      [buildUpdateRequest]="buildUpdateRequest"
    ></app-entity-crud-table>
  `,
})
export class BatimentPageComponent {
  protected readonly service = inject(BatimentService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  protected readonly columns: DataTableColumn<BatimentDto>[] = [
    { key: 'nom', header: 'Nom', accessor: (r) => r.nom },
    { key: 'etages', header: 'Étages', accessor: (r) => String(r.etages.length) },
  ];

  protected readonly fields: EntityFieldConfig<BatimentDto>[] = [{ key: 'nom', label: 'Nom', required: true }];

  protected readonly emptyDto = (): BatimentDto => ({ id: null, nom: '', etages: [] });

  protected readonly buildUpdateRequest = (id: string, dto: BatimentDto): BatimentUpdateRequest => ({ id, dto });
}
