package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import org.aminesidki.resiaiac.dto.entry.EquipementEntry;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

public record MyReclamationRequest(
    @OptionalNotBlank String message,
    @NotNull Long service,
    @Valid List<EquipementEntry> equipements)
    implements Serializable {}
