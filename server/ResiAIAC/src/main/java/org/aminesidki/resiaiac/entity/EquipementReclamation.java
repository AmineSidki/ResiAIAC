package org.aminesidki.resiaiac.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class EquipementReclamation {
    @EmbeddedId
    private EquipementReclamationId id;

    private Long quantite;

    @ManyToOne
    @JoinColumn(name = "equipement_id")
    private Equipement equipement;

    @ManyToOne
    @JoinColumn(name = "reclamation_id")
    private Reclamation reclamation;
}
