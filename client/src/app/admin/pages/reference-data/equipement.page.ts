import { Component, computed, inject } from '@angular/core';
import { EntityCrudTableComponent, EntityFieldConfig } from '../../shared/entity-crud-table/entity-crud-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { EquipementService } from '../../../core/services/equipement.service';
import { EquipementDto, EquipementUpdateRequest } from '../../../core/models/dtos';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';

/**
 * Equipement is the one entity in this reference-data group whose writes
 * are MANAGER-gated rather than RESPONSABLE (confirmed against
 * EquipementController.java) — every plain MANAGER can add/edit/delete
 * equipment types, unlike Batiment/Etage/Filiere/Service.
 */
@Component({
  selector: 'app-equipement-page',
  standalone: true,
  imports: [EntityCrudTableComponent],
  template: `
    <app-entity-crud-table
      [service]="service"
      [columns]="columns"
      [fields]="fields"
      title="Équipements"
      entityLabel="un équipement"
      [canWrite]="canWrite()"
      [emptyDto]="emptyDto"
      [buildUpdateRequest]="buildUpdateRequest"
    ></app-entity-crud-table>
  `,
})
export class EquipementPageComponent {
  protected readonly service = inject(EquipementService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'MANAGER'));

  protected readonly columns: DataTableColumn<EquipementDto>[] = [
    { key: 'nom', header: 'Nom', accessor: (r) => r.nom },
    { key: 'reclamations', header: 'Réclamations liées', accessor: (r) => String(r.reclamations.length) },
    { key: 'upcs', header: "Chambres équipées", accessor: (r) => String(r.upcs.length) },
  ];

  protected readonly fields: EntityFieldConfig<EquipementDto>[] = [{ key: 'nom', label: 'Nom', required: true }];

  protected readonly emptyDto = (): EquipementDto => ({ id: null, nom: '', reclamations: [], upcs: [] });

  protected readonly buildUpdateRequest = (id: number, dto: EquipementDto): EquipementUpdateRequest => ({ id, dto });
}
