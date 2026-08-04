package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurPromotionChambre {
    @EmbeddedId
    private UtilisateurPromotionChambreId id;
    private Boolean retard;
    private String note;

    @MapsId("utilisateurId")
    @ManyToOne
    @JoinColumn(name = "utilisateur")
    private Utilisateur utilisateur;

    @MapsId("promotionId")
    @ManyToOne
    @JoinColumn(name = "promotion")
    private Promotion promotion;

    @MapsId("chambreId")
    @ManyToOne
    @JoinColumn(name = "chambre")
    private Chambre chambre;

    @OneToMany
    private List<EquipementUpc> equipementsEndommages;
}
