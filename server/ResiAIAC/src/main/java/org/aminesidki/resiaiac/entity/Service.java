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
public class Service {
  @Id
  @Column(updatable = false, insertable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String nom;

  @OneToMany(mappedBy = "service")
  private List<Reclamation> reclamations;
}
