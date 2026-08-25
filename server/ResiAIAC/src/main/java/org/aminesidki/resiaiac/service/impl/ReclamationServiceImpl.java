package org.aminesidki.resiaiac.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.EquipementReclamationDto;
import org.aminesidki.resiaiac.dto.ReclamationDto;
import org.aminesidki.resiaiac.dto.request.MyReclamationRequest;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.aminesidki.resiaiac.exception.ResourceOwnershipMismatchException;
import org.aminesidki.resiaiac.mapper.ReclamationMapper;
import org.aminesidki.resiaiac.repository.ReclamationRepository;
import org.aminesidki.resiaiac.service.*;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ReclamationServiceImpl implements ReclamationService {

  private final UtilisateurService utilisateurService;
  private final UtilisateurPromotionChambreService utilisateurPromotionChambreService;
  private final EquipementReclamationService equipementReclamationService;
  private final ReclamationRepository reclamationRepository;
  private final ReclamationMapper reclamationMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<ReclamationDto> getAllMyByStatus(Jwt jwt, EtatReclamation etat, Pageable pageable) {
    Utilisateur id = utilisateurService.getMyEntity(jwt);
    return reclamationRepository
        .findAllByUtilisateurAndEtat(id, etat, pageable)
        .map(reclamationMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ReclamationDto> getAllMy(Jwt jwt, Pageable pageable) {
    Utilisateur id = utilisateurService.getMyEntity(jwt);
    return reclamationRepository.findAllByUtilisateur(id, pageable).map(reclamationMapper::toDto);
  }

  @Override
  public ReclamationDto saveMy(Jwt jwt, MyReclamationRequest request) {
    // fetch user // fetch room = fetch UPC with highest endYear // fetch the equipements
    // create dto // map dto -> entity // persist // create composite // persist composite
    Utilisateur id = utilisateurService.getMyEntity(jwt);
    Chambre chambre = utilisateurPromotionChambreService.getCurrentRoomByUser(id);

    ReclamationDto dto = reclamationMapper.myReclamationToDto(request);

    Reclamation entity = reclamationMapper.toEntity(dto);
    entity.setEtat(EtatReclamation.EN_ATTENTE);
    entity.setChambre(chambre);
    entity.setUtilisateur(id);
    entity = reclamationRepository.save(entity);

    final UUID entityId = entity.getId();
    request
        .equipements()
        .forEach(
            e ->
                equipementReclamationService.save(
                    new EquipementReclamationDto(null, e.quantite(), e.id(), entityId)));
    return reclamationMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public ReclamationDto getMyById(Jwt jwt, UUID id) {
    Reclamation entity = ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation");
    Utilisateur utilisateur = utilisateurService.getMyEntity(jwt);
    if (entity.getUtilisateur().getId().equals(utilisateur.getId())) {
      return reclamationMapper.toDto(entity);
    }
    throw new ResourceOwnershipMismatchException(
        "Queried resource does not belong to querying user !");
  }

  @Override
  public Page<ReclamationDto> getAllByStatus(EtatReclamation etat, Pageable pageable) {
    return reclamationRepository.findAllByEtat(etat, pageable).map(reclamationMapper::toDto);
  }

  @Override
  public Page<ReclamationDto> getAll(Pageable pageable) {
    return reclamationRepository.findAll(pageable).map(reclamationMapper::toDto);
  }

  @Override
  public ReclamationDto save(ReclamationDto dto) {
    Reclamation entity = reclamationMapper.toEntity(dto);
    entity = reclamationRepository.save(entity);
    return reclamationMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public ReclamationDto getById(UUID id) {
    Reclamation entity = ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation");
    return reclamationMapper.toDto(entity);
  }

  @Override
  public ReclamationDto update(UUID id, ReclamationDto dto) {
    Reclamation entity = ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation");
    reclamationMapper.updateEntityFromDto(dto, entity);
    entity = reclamationRepository.save(entity);
    return reclamationMapper.toDto(entity);
  }

  @Override
  public void delete(UUID id) {
    reclamationRepository.delete(
        ResourceFetcher.fetchResource(id, reclamationRepository, "Reclamation"));
  }
}
