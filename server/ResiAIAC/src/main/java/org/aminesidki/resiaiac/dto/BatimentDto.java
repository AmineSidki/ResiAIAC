package org.aminesidki.resiaiac.dto;

import java.io.Serializable;
import java.util.UUID;

public record BatimentDto(UUID id, String nom) implements Serializable {}
