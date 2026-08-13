package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.EtageDto;

public record EtageUpdateRequest(UUID id, EtageDto dto) {}
