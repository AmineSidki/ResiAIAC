package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurPromotionChambre {
  @EmbeddedId
  @Column(updatable = false, insertable = false)
  private UtilisateurPromotionChambreId id;

  private Boolean retard;
  private String note;

  @MapsId("utilisateur_id")
  @ManyToOne
  @JoinColumn(name = "utilisateur_id")
  private Utilisateur utilisateur;

  @MapsId("promotion_id")
  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;

  @MapsId("chambre_id")
  @ManyToOne
  @JoinColumn(name = "chambre_id")
  private Chambre chambre;

  @OneToMany(mappedBy = "upc")
  private List<EquipementUpc> equipementsEndommages;
}
