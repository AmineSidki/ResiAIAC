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
public class Etage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String numero;

    @ManyToOne
    @JoinColumn(name = "batiment")
    private Batiment batiment;

    @OneToMany(mappedBy = "etage")
    private List<Chambre> chambres;
}
