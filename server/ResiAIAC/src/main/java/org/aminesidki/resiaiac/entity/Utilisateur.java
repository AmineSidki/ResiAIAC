package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Utilisateur {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String nom;
  private String prenom;
  private String cin;
  private String adresse;
  private String telephone;

  @OneToMany(mappedBy = "utilisateur")
  private List<Reservation> reservations;

  @OneToMany(mappedBy = "utilisateur")
  private List<Reclamation> reclamations;

  @OneToMany(mappedBy = "proprietaire")
  private List<Document> documents;

  @OneToMany(mappedBy = "utilisateur")
  private List<UtilisateurPromotionChambre> combinaisonsUpc;

  @CreationTimestamp private Timestamp createdAt;
  @UpdateTimestamp private Timestamp updatedAt;
}
