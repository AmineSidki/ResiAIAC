package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class EquipementUpcId {
    private Long equipementId;
    private UtilisateurPromotionChambreId utilisateurPromotionChambreId;
}
