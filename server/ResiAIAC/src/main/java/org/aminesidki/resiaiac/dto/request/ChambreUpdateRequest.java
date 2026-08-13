package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ChambreDto;

public record ChambreUpdateRequest(UUID id, ChambreDto dto) {}
