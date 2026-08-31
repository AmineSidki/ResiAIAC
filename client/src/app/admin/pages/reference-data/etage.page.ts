import { Component, computed, inject, signal } from '@angular/core';
import { EntityCrudTableComponent, EntityFieldConfig } from '../../shared/entity-crud-table/entity-crud-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.component';
import { EtageService } from '../../../core/services/etage.service';
import { BatimentService } from '../../../core/services/batiment.service';
import { EtageDto, EtageUpdateRequest } from '../../../core/models/dtos';
import { CurrentUserService } from '../../../core/auth/current-user.service';
import { hasRoleAtLeast } from '../../../core/auth/role.guard';

/** Etage: RESPONSABLE-gated writes, list open to any authenticated user (confirmed against EtageController.java). */
@Component({
  selector: 'app-etage-page',
  standalone: true,
  imports: [EntityCrudTableComponent],
  template: `
    <app-entity-crud-table
      [service]="service"
      [columns]="columns()"
      [fields]="fields()"
      title="Étages"
      entityLabel="un étage"
      [canWrite]="canWrite()"
      [emptyDto]="emptyDto"
      [buildUpdateRequest]="buildUpdateRequest"
    ></app-entity-crud-table>
  `,
})
export class EtagePageComponent {
  protected readonly service = inject(EtageService);
  private readonly batimentService = inject(BatimentService);
  private readonly currentUser = inject(CurrentUserService);

  protected readonly canWrite = computed(() => hasRoleAtLeast(this.currentUser.realmRoles(), 'RESPONSABLE'));

  private readonly batimentNameById = signal<Map<string, string>>(new Map());

  protected readonly columns = computed<DataTableColumn<EtageDto>[]>(() => {
    const names = this.batimentNameById();
    return [
      { key: 'numero', header: 'Numéro', accessor: (r) => r.numero },
      { key: 'batiment', header: 'Bâtiment', accessor: (r) => names.get(r.batiment) ?? r.batiment },
      { key: 'chambres', header: 'Chambres', accessor: (r) => String(r.chambres.length) },
    ];
  });

  protected readonly fields = computed<EntityFieldConfig<EtageDto>[]>(() => [
    { key: 'numero', label: 'Numéro', required: true },
    {
      key: 'batiment',
      label: 'Bâtiment',
      type: 'select',
      required: true,
      options: Array.from(this.batimentNameById(), ([value, label]) => ({ value, label })),
    },
  ]);

  protected readonly emptyDto = (): EtageDto => ({ id: null, numero: '', batiment: '', chambres: [] });

  protected readonly buildUpdateRequest = (id: string, dto: EtageDto): EtageUpdateRequest => ({ id, dto });

  constructor() {
    this.batimentService.getAll().subscribe((batiments) => {
      this.batimentNameById.set(new Map(batiments.map((b) => [b.id as string, b.nom])));
    });
  }
}
