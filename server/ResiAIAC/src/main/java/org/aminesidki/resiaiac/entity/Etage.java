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
public class Etage {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String numero;

  @ManyToOne
  @JoinColumn(name = "batiment_id")
  private Batiment batiment;

  @OneToMany(mappedBy = "etage")
  private List<Chambre> chambres;
}
