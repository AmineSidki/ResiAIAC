import { Injectable } from '@angular/core';
import { BaseCrudService } from '../api/base-crud.service';
import { EquipementDto, EquipementUpdateRequest } from '../models/dtos';

/** Writes are MANAGER-gated; getAll/getById open to any authenticated user (same pattern as the other reference tables). */
@Injectable({ providedIn: 'root' })
export class EquipementService extends BaseCrudService<EquipementDto, EquipementUpdateRequest, number> {
  protected readonly resourcePath = 'equipement';
}
