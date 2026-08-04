package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long anneeDeDepart;
    private Long anneeDeFin;

    @ManyToOne
    @JoinColumn(name = "filiere")
    private Filiere filiere;

    @OneToMany
    private List<UtilisateurPromotionChambre> combinaisonsUpc;
}
