package org.aminesidki.resiaiac.dto.entry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EquipementEntry(@NotNull Long id, @Min(1) Long quantite) {}
