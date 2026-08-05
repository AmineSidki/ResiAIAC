package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.enumeration.EtatChambre;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Chambre {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String matricule;
    private Long capacite;
    private EtatChambre etat;

    @OneToMany(mappedBy = "chambre")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "chambre")
    private List<Reclamation> reclamations;

    @OneToMany(mappedBy = "chambre")
    private List<UtilisateurPromotionChambre>  combinaisonsUpc;

    @ManyToOne
    @JoinColumn(name = "etage")
    private Etage etage;
}
