import { Injectable } from '@angular/core';
import { BaseCrudService } from '../api/base-crud.service';
import { EtageDto, EtageUpdateRequest } from '../models/dtos';

/** RESPONSABLE-gated writes; getAll/getById open to any authenticated user. */
@Injectable({ providedIn: 'root' })
export class EtageService extends BaseCrudService<EtageDto, EtageUpdateRequest, string> {
  protected readonly resourcePath = 'etage';
}
