import { Injectable } from '@angular/core';
import { BaseCrudService } from '../api/base-crud.service';
import { ChambreDto, ChambreUpdateRequest } from '../models/dtos';

/** RESPONSABLE-gated writes; getAll/getById open to any authenticated user (deliberate self-service reads: a student can look up any room). */
@Injectable({ providedIn: 'root' })
export class ChambreService extends BaseCrudService<ChambreDto, ChambreUpdateRequest, string> {
  protected readonly resourcePath = 'chambre';
}
