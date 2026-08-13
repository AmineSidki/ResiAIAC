package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipementUpc {
  @EmbeddedId
  @Column(updatable = false, insertable = false)
  private EquipementUpcId id;

  private Long quantite;

  @MapsId("equipement_id")
  @ManyToOne
  @JoinColumn(name = "equipement_id")
  private Equipement equipement;

  @MapsId("utilisateurPromotionChambre_id")
  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "upc_utilisateur", referencedColumnName = "utilisateur_id"),
    @JoinColumn(name = "upc_promotion", referencedColumnName = "promotion_id"),
    @JoinColumn(name = "upc_chambre", referencedColumnName = "chambre_id")
  })
  private UtilisateurPromotionChambre upc;
}
