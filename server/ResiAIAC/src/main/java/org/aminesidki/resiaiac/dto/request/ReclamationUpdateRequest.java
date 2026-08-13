package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.ReclamationDto;

public record ReclamationUpdateRequest(UUID id, ReclamationDto dto) {}
