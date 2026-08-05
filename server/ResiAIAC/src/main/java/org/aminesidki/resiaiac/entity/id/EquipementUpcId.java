package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class EquipementUpcId {
  private Long equipement_id;
  private UtilisateurPromotionChambreId utilisateurPromotionChambre_id;
}
