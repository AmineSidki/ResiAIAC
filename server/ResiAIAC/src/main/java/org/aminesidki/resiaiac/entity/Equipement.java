package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Equipement {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String nom;

  @OneToMany(mappedBy = "equipement")
  private List<EquipementReclamation> reclamations;

  @OneToMany(mappedBy = "equipement")
  private List<EquipementUpc> upcs;
}
