package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Batiment } */
public record BatimentDto(UUID id, String nom, List<UUID> etages) implements Serializable {}
