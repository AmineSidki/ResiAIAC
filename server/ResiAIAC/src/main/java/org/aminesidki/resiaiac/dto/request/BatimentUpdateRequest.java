package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.BatimentDto;

public record BatimentUpdateRequest(UUID id, BatimentDto dto) {}
