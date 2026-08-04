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
public class Batiment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nom;

    @OneToMany
    private List<Etage> etages;
}
