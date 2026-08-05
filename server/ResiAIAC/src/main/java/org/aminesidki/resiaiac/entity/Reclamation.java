package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reclamation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String message;
  private EtatReclamation etat;

  @ManyToOne
  @JoinColumn(name = "utilisateur_id")
  private Utilisateur utilisateur;

  @ManyToOne
  @JoinColumn(name = "chambre_id")
  private Chambre chambre;

  @ManyToOne
  @JoinColumn(name = "service_id")
  private Service service;

  @OneToMany(mappedBy = "reclamation")
  private List<EquipementReclamation> equipements;

  @CreationTimestamp private Timestamp createdAt;
}
