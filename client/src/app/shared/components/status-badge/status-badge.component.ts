import { Component, computed, input } from '@angular/core';
import { EtatChambre, EtatDocument, EtatReclamation, EtatReservation } from '../../../core/models/enums';

type AnyEtat = EtatDocument | EtatReclamation | EtatReservation | EtatChambre;

interface BadgeStyle {
  label: string;
  classes: string;
}

/**
 * Single source of truth for how every Etat* enum renders as a badge.
 * Grouped by entity because the same raw value (e.g. "FERMEE") can mean
 * different things across enums — never key this map by value alone.
 */
const DOCUMENT_STYLES: Record<EtatDocument, BadgeStyle> = {
  AUCUN: { label: 'Aucun document', classes: 'bg-neutral-100 text-neutral-700' },
  EN_ATTENTE: { label: 'En attente', classes: 'bg-accent-500/10 text-accent-600' },
  VALIDE: { label: 'Validé', classes: 'bg-success-500/10 text-success-500' },
  INVALIDE: { label: 'Invalide', classes: 'bg-danger-500/10 text-danger-500' },
};

const RECLAMATION_STYLES: Record<EtatReclamation, BadgeStyle> = {
  EN_ATTENTE: { label: 'En attente', classes: 'bg-accent-500/10 text-accent-600' },
  EN_TRAITEMENT: { label: 'En traitement', classes: 'bg-primary-500/10 text-primary-600' },
  FERME_TRAITE: { label: 'Fermée (traitée)', classes: 'bg-success-500/10 text-success-500' },
  FERME_SANS_TRAITEMENT: { label: 'Fermée (sans traitement)', classes: 'bg-neutral-100 text-neutral-700' },
};

const RESERVATION_STYLES: Record<EtatReservation, BadgeStyle> = {
  ACTIVE: { label: 'Active', classes: 'bg-success-500/10 text-success-500' },
  TERMINEE: { label: 'Terminée', classes: 'bg-neutral-100 text-neutral-700' },
  FERMEE: { label: 'Fermée', classes: 'bg-danger-500/10 text-danger-500' },
};

const CHAMBRE_STYLES: Record<EtatChambre, BadgeStyle> = {
  LIBRE: { label: 'Libre', classes: 'bg-success-500/10 text-success-500' },
  PARTIELLEMENT_LIBRE: { label: 'Partiellement libre', classes: 'bg-accent-500/10 text-accent-600' },
  MAINTENANCE: { label: 'Maintenance', classes: 'bg-neutral-100 text-neutral-700' },
  OCCUPEE: { label: 'Occupée', classes: 'bg-danger-500/10 text-danger-500' },
};

const STYLE_MAPS = {
  document: DOCUMENT_STYLES,
  reclamation: RECLAMATION_STYLES,
  reservation: RESERVATION_STYLES,
  chambre: CHAMBRE_STYLES,
} as const;

export type StatusBadgeKind = keyof typeof STYLE_MAPS;

@Component({
  selector: 'app-status-badge',
  standalone: true,
  template: `
    <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium" [class]="style().classes">
      {{ style().label }}
    </span>
  `,
})
export class StatusBadgeComponent {
  readonly kind = input.required<StatusBadgeKind>();
  readonly value = input.required<AnyEtat>();

  protected readonly style = computed<BadgeStyle>(() => {
    const map = STYLE_MAPS[this.kind()] as Record<string, BadgeStyle>;
    return map[this.value()] ?? { label: this.value(), classes: 'bg-neutral-100 text-neutral-700' };
  });
}
