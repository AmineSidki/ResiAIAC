package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.hibernate.annotations.CreationTimestamp;

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
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String message;
    private EtatReclamation etat;

    @ManyToOne
    @JoinColumn(name = "utilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "chambre")
    private Chambre chambre;

    @ManyToOne
    @JoinColumn(name = "service")
    private Service service;

    @OneToMany
    private List<EquipementReclamation> equipements;

    @CreationTimestamp
    private Timestamp createdAt;
}
