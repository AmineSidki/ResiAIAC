package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;
import org.aminesidki.resiaiac.enumeration.EtatReservation;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private EtatReservation etat;

  @ManyToOne
  @JoinColumn(name = "utilisateur_id")
  private Utilisateur utilisateur;

  @ManyToOne
  @JoinColumn(name = "chambre_id")
  private Chambre chambre;

  @CreationTimestamp private Timestamp createdAt;
}
