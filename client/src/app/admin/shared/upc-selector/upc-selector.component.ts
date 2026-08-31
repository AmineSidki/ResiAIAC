import { Component, OnInit, inject, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { map } from 'rxjs';
import { UtilisateurService } from '../../../core/services/utilisateur.service';
import { PromotionService } from '../../../core/services/promotion.service';
import { ChambreService } from '../../../core/services/chambre.service';
import { UtilisateurPromotionChambreId } from '../../../core/models/ids';
import { SelectComponent, SelectOption } from '../../../shared/components/select/select.component';
import { IdLookupFieldComponent } from '../id-lookup-field/id-lookup-field.component';

/**
 * Shared {utilisateurId, promotionId, chambreId} picker — built once here
 * and reused for every UPC-keyed screen (assignment creation, the
 * EquipementUpc checklist, and anywhere else this track needs the same
 * triple). Promotion (MANAGER-listable) and Chambre (open list) render as
 * dropdowns; Utilisateur falls back to id-lookup since a plain MANAGER
 * can't list users. buildCompositeParams() (Phase 0) handles the query-string
 * mechanics on the service side — this component is purely the picker UI.
 */
@Component({
  selector: 'app-upc-selector',
  standalone: true,
  imports: [FormsModule, SelectComponent, IdLookupFieldComponent],
  template: `
    <div class="grid grid-cols-1 gap-3 sm:grid-cols-3">
      <app-id-lookup-field
        label="Étudiant (UUID)"
        placeholder="ID de l'utilisateur"
        [lookup]="lookupUtilisateur"
        (resolved)="onUtilisateur($event)"
      ></app-id-lookup-field>

      <app-select
        label="Promotion"
        placeholder="Choisir une promotion"
        [options]="promotionOptions()"
        (ngModelChange)="onPromotion($event)"
        [ngModel]="promotionId()"
      ></app-select>

      <app-select
        label="Chambre"
        placeholder="Choisir une chambre"
        [options]="chambreOptions()"
        (ngModelChange)="onChambre($event)"
        [ngModel]="chambreId()"
      ></app-select>
    </div>
  `,
})
export class UpcSelectorComponent implements OnInit {
  private readonly utilisateurService = inject(UtilisateurService);
  private readonly promotionService = inject(PromotionService);
  private readonly chambreService = inject(ChambreService);

  readonly selected = output<UtilisateurPromotionChambreId | null>();

  protected readonly utilisateurId = signal<string | null>(null);
  protected readonly promotionId = signal<string | null>(null);
  protected readonly chambreId = signal<string | null>(null);

  protected readonly promotionOptions = signal<SelectOption[]>([]);
  protected readonly chambreOptions = signal<SelectOption[]>([]);

  protected readonly lookupUtilisateur = (id: string) =>
    this.utilisateurService.getById(id).pipe(map((u) => ({ label: `${u.prenom} ${u.nom} (${u.email})` })));

  ngOnInit(): void {
    // First page only (20 max per server-side ceiling) — acceptable for an
    // MVP picker; a real search endpoint would remove this limitation.
    this.promotionService.getAll({ size: 20 }).subscribe((page) => {
      this.promotionOptions.set(
        page.content.map((p) => ({
          value: p.id as string,
          label: `${p.anneeDeDepart}-${p.anneeDeFin} · niveau ${p.niveau}`,
        })),
      );
    });
    this.chambreService.getAll().subscribe((rooms) => {
      this.chambreOptions.set(rooms.map((c) => ({ value: c.id as string, label: c.matricule })));
    });
  }

  protected onUtilisateur(id: string | null): void {
    this.utilisateurId.set(id);
    this.emit();
  }

  protected onPromotion(id: string): void {
    this.promotionId.set(id);
    this.emit();
  }

  protected onChambre(id: string): void {
    this.chambreId.set(id);
    this.emit();
  }

  private emit(): void {
    const u = this.utilisateurId();
    const p = this.promotionId();
    const c = this.chambreId();
    if (u && p && c) {
      this.selected.emit({ utilisateur_id: u, promotion_id: p, chambre_id: c });
    } else {
      this.selected.emit(null);
    }
  }
}
