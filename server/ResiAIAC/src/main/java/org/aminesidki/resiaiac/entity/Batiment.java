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
public class Batiment {
  @Id
  @Column(updatable = false, insertable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String nom;

  @OneToMany(mappedBy = "batiment")
  private List<Etage> etages;
}
