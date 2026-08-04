package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class EquipementReclamationId {
    private Long equipementId;
    private UUID reclamationId;
}
