package org.aminesidki.resiaiac.dto.request;

import org.aminesidki.resiaiac.dto.BatimentDto;

import java.util.UUID;

public record BatimentUpdateRequest(UUID id, BatimentDto dto) {
}
