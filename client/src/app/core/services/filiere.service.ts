import { Injectable } from '@angular/core';
import { BaseCrudService } from '../api/base-crud.service';
import { FiliereDto, FiliereUpdateRequest } from '../models/dtos';

/** RESPONSABLE-gated writes; getAll/getById open to any authenticated user. */
@Injectable({ providedIn: 'root' })
export class FiliereService extends BaseCrudService<FiliereDto, FiliereUpdateRequest, number> {
  protected readonly resourcePath = 'filiere';
}
