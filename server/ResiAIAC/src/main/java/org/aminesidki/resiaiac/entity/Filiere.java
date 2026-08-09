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
public class Filiere {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String nom;
  private Integer niveauMaximal;

  @OneToMany(mappedBy = "filiere")
  private List<Promotion> promotions;
}
