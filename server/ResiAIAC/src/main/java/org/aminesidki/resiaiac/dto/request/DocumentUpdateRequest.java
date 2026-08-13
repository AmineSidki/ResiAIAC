package org.aminesidki.resiaiac.dto.request;

import java.util.UUID;
import org.aminesidki.resiaiac.dto.DocumentDto;

public record DocumentUpdateRequest(UUID id, DocumentDto dto) {}
