package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipementReclamation {
  @EmbeddedId private EquipementReclamationId id;

  private Long quantite;

  @MapsId("equipement_id")
  @ManyToOne
  @JoinColumn(name = "equipement_id")
  private Equipement equipement;

  @MapsId("reclamation_id")
  @ManyToOne
  @JoinColumn(name = "reclamation_id")
  private Reclamation reclamation;
}
