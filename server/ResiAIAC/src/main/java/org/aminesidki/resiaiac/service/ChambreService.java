package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.ChambreDto;

import java.util.UUID;

public interface ChambreService {
    ChambreDto save(ChambreDto dto);
    ChambreDto getById(UUID id);
    ChambreDto update(UUID id, ChambreDto dto);
    void delete(UUID id);
}
