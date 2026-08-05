package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class EquipementUpc {
    @EmbeddedId
    private EquipementUpcId id;
    private Long quantite;

    @MapsId("equipementId")
    @ManyToOne
    @JoinColumn(name = "equipement_id")
    private Equipement equipement;

    @MapsId("utilisateurPromotionChambreId")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "upc_utilisateur", referencedColumnName = "utilisateurId"),
            @JoinColumn(name = "upc_promotion", referencedColumnName = "promotionId"),
            @JoinColumn(name = "upc_chambre", referencedColumnName = "chambreId")
    })
    private UtilisateurPromotionChambre upc;
}
