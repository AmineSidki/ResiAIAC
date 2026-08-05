package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UtilisateurPromotionChambreId {
    private UUID utilisateurId;
    private UUID promotionId;
    private UUID chambreId;
}
