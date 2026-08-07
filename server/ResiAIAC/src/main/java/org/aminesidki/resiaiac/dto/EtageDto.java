package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Dto for {@link org.aminesidki.resiaiac.entity.Etage } */
public record EtageDto(UUID id, String numero, UUID batiment, List<UUID> chambres)
    implements Serializable {}