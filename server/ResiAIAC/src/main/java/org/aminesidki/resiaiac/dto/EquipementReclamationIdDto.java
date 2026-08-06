package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;

public record EquipementReclamationIdDto(Long equipement_id, UUID reclamation_id)
    implements Serializable {}
