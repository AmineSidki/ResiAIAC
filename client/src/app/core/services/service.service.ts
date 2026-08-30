import { Injectable } from '@angular/core';
import { BaseCrudService } from '../api/base-crud.service';
import { ServiceDto, ServiceUpdateRequest } from '../models/dtos';

/**
 * Named ServiceService to avoid colliding with the backend's own "Service"
 * naming ambiguity with Angular's @Injectable — this is the client for the
 * `Service` reference entity (e.g. "Plomberie", "Electricité" — the category
 * a Reclamation is filed against), not a generic app service.
 * RESPONSABLE-gated writes; getAll/getById open to any authenticated user.
 */
@Injectable({ providedIn: 'root' })
export class ServiceEntityService extends BaseCrudService<ServiceDto, ServiceUpdateRequest, number> {
  protected readonly resourcePath = 'service';
}
