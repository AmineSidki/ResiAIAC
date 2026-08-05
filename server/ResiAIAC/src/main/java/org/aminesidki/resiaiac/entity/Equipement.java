package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    private String nom;

    @OneToMany(mappedBy = "equipement")
    private List<EquipementReclamation> reclamations;

    @OneToMany(mappedBy = "equipement")
    private List<EquipementUpc> upcs;
}
