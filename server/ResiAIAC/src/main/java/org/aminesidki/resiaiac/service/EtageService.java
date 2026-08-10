package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.EtageDto;

import java.util.UUID;

public interface EtageService {
    EtageDto save(EtageDto dto);
    EtageDto getById(UUID id);
    EtageDto update(UUID id, EtageDto dto);
    void delete(UUID id);
}
