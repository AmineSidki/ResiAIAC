package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Batiment;

public record EtageDto(UUID id, String numero, Batiment batiment) implements Serializable {}
