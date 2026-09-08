package org.aminesidki.resiaiac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import org.aminesidki.resiaiac.enumeration.Role;
import org.aminesidki.resiaiac.validator.OptionalNotBlank;

/** Dto for {@link org.aminesidki.resiaiac.entity.Utilisateur } */
public record UtilisateurDto(
    UUID id,
    Role role,
    @NotBlank(message = "Email ne peut pas etre vide !") @Email(message = "Email invalide !")
        String email,
    @NotBlank(message = "Nom ne peut pas etre vide !") String nom,
    @NotBlank(message = "Prenom ne peut pas etre vide !") String prenom,
    @NotBlank(message = "CIN invalide !") String cin,
    @OptionalNotBlank String adresse,
    @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numero de telephone invalide !")
        String telephone,
    Long filiere,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) Timestamp createdAt,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) Timestamp updatedAt)
    implements Serializable {}
