package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String nomFichier;
  private String nomSceau;
  private Boolean valide;
  private String noteSurValidite;

  @ManyToOne
  @JoinColumn(name = "proprietaire_id")
  private Utilisateur proprietaire;

  @CreationTimestamp private Timestamp createdAt;
}
