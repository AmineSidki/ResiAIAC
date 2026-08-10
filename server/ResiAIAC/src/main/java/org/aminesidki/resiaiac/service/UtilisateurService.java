package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.dto.UtilisateurDto;

import java.util.UUID;

public interface UtilisateurService {
    UtilisateurDto save(UtilisateurDto dto);
    UtilisateurDto getById(UUID id);
    UtilisateurDto update(UUID id, UtilisateurDto dto);
    void delete(UUID id);
}
