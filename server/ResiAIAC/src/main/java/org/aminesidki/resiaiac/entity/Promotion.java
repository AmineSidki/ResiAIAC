package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Long anneeDeDepart;
  private Long anneeDeFin;

  @ManyToOne
  @JoinColumn(name = "filiere_id")
  private Filiere filiere;

  @OneToMany(mappedBy = "promotion")
  private List<UtilisateurPromotionChambre> combinaisonsUpc;
}
