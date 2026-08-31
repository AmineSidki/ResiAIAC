import { Component, computed, inject } from '@angular/core';
import { EntityCrudTableComponent, EntityFieldConfig } from '../../shared/entity-crud-table/entity-crud-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { ServiceEntityService } from '../../../core/services/service.service';
import { ServiceDto, ServiceUpdateRequest } from '../../../core/models/dtos';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';

/**
 * "Service" here is the reference-data entity used by Reclamation.service
 * (e.g. "Plomberie", "Électricité") — not to be confused with Angular
 * services. RESPONSABLE-gated writes (confirmed against ServiceController.java).
 * Named `ServiceEntityPageComponent` to avoid the same naming clash the
 * Phase 0 API layer already sidesteps with `ServiceEntityService`.
 */
@Component({
  selector: 'app-service-entity-page',
  standalone: true,
  imports: [EntityCrudTableComponent],
  template: `
    <app-entity-crud-table
      [service]="service"
      [columns]="columns"
      [fields]="fields"
      title="Services"
      subtitle="Catégories utilisées pour classer les réclamations."
      entityLabel="un service"
      [canWrite]="canWrite()"
      [emptyDto]="emptyDto"
      [buildUpdateRequest]="buildUpdateRequest"
    ></app-entity-crud-table>
  `,
})
export class ServiceEntityPageComponent {
  protected readonly service = inject(ServiceEntityService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  protected readonly columns: DataTableColumn<ServiceDto>[] = [
    { key: 'nom', header: 'Nom', accessor: (r) => r.nom },
    { key: 'reclamations', header: 'Réclamations liées', accessor: (r) => String(r.reclamations.length) },
  ];

  protected readonly fields: EntityFieldConfig<ServiceDto>[] = [{ key: 'nom', label: 'Nom', required: true }];

  protected readonly emptyDto = (): ServiceDto => ({ id: null, nom: '', reclamations: [] });

  protected readonly buildUpdateRequest = (id: number, dto: ServiceDto): ServiceUpdateRequest => ({ id, dto });
}
