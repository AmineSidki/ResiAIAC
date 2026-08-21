package org.aminesidki.resiaiac.dto.request;

import jakarta.validation.constraints.Pattern;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

public record UpdateMeRequest(
    @OptionalNotBlank String adresse,
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numero de telephone invalide !")
        String telephone) {}
