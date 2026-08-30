import { Injectable } from '@angular/core';
import { BaseCrudService } from '../api/base-crud.service';
import { BatimentDto, BatimentUpdateRequest } from '../models/dtos';

/** RESPONSABLE-gated writes; getAll/getById open to any authenticated user. */
@Injectable({ providedIn: 'root' })
export class BatimentService extends BaseCrudService<BatimentDto, BatimentUpdateRequest, string> {
  protected readonly resourcePath = 'batiment';
}
