import { Component, computed, inject } from '@angular/core';
import { EntityCrudTableComponent, EntityFieldConfig } from '../../shared/entity-crud-table/entity-crud-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { FiliereService } from '../../../core/services/filiere.service';
import { FiliereDto, FiliereUpdateRequest } from '../../../core/models/dtos';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';

/** Filiere: RESPONSABLE-gated writes, list open to any authenticated user (confirmed against FiliereController.java). */
@Component({
  selector: 'app-filiere-page',
  standalone: true,
  imports: [EntityCrudTableComponent],
  template: `
    <app-entity-crud-table
      [service]="service"
      [columns]="columns"
      [fields]="fields"
      title="Filières"
      entityLabel="une filière"
      [canWrite]="canWrite()"
      [emptyDto]="emptyDto"
      [buildUpdateRequest]="buildUpdateRequest"
    ></app-entity-crud-table>
  `,
})
export class FilierePageComponent {
  protected readonly service = inject(FiliereService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  protected readonly columns: DataTableColumn<FiliereDto>[] = [
    { key: 'nom', header: 'Nom', accessor: (r) => r.nom },
    { key: 'niveauMaximal', header: 'Niveau max.', accessor: (r) => String(r.niveauMaximal) },
  ];

  protected readonly fields: EntityFieldConfig<FiliereDto>[] = [
    { key: 'nom', label: 'Nom', required: true },
    { key: 'niveauMaximal', label: 'Niveau maximal', type: 'number', required: true, min: 1 },
  ];

  protected readonly emptyDto = (): FiliereDto => ({ id: null, nom: '', niveauMaximal: 1 });

  protected readonly buildUpdateRequest = (id: number, dto: FiliereDto): FiliereUpdateRequest => ({ id, dto });
}
