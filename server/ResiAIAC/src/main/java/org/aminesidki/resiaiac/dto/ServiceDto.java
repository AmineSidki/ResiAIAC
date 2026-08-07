package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Service } */
public record ServiceDto(Long id, String nom, List<UUID> reclamations) implements Serializable {}