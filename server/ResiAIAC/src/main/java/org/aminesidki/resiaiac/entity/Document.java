package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue
    private UUID id;

    private String nomFichier;
    private String nomSceau;
    private Boolean valide;
    private String noteSurValidite;

    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private Utilisateur proprietaire;

    @CreationTimestamp
    private Timestamp createdAt;
}
