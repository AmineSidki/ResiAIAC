package org.aminesidki.resiaiac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

/** Dto for {@link org.aminesidki.resiaiac.entity.Reclamation } */
public record ReclamationDto(
    UUID id,
    @OptionalNotBlank String message,
    EtatReclamation etat,
    @NotNull UUID utilisateur,
    @NotNull UUID chambre,
    @NotNull Long service,
    List<EquipementReclamationId> equipements,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) Timestamp createdAt,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) Timestamp updatedAt)
    implements Serializable {}
