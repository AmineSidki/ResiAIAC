package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurDto;

public record UtilisateurUpdateRequest(@NotNull UUID id, @NotNull @Valid UtilisateurDto dto) {}
