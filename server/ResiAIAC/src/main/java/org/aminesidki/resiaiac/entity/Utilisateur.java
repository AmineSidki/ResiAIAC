package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
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

    @OneToMany
    private List<Reservation> reservations;

    @OneToMany
    private List<Reclamation> reclamations;

    @OneToMany
    private List<Document> documents;

    @OneToMany
    private List<UtilisateurPromotionChambre>  combinaisonsUpc;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}
