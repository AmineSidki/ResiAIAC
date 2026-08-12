package org.aminesidki.resiaiac.controller.test;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.dto.*;
import org.aminesidki.resiaiac.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/composite-id-persistence-test")
public class CompositeIdPersitenceTestController {
  private final EquipementReclamationService equipementReclamationService;
  private final EquipementService equipementService;
  private final ReclamationService reclamationService;
  private final UtilisateurService utilisateurService;
  private final ChambreService chambreService;
  private final EtageService etageService;
  private final BatimentService batimentService;
  private final ServiceService serviceService;

  public record TestRequestBody(
      UtilisateurDto utilisateurDto,
      BatimentDto batimentDto,
      EtageDto etageDto,
      ChambreDto chambreDto,
      ServiceDto serviceDto,
      ReclamationDto reclamationDto,
      EquipementDto equipementDto) {}

  @PostMapping("/")
  public ResponseEntity<?> testEndpoint(@RequestBody TestRequestBody body) {
    BatimentDto batimentDto =
        batimentService.save(new BatimentDto(null, body.batimentDto().nom(), null));

    EtageDto etageDto =
        etageService.save(new EtageDto(null, body.etageDto().numero(), batimentDto.id(), null));

    ChambreDto chambreDto =
        chambreService.save(
            new ChambreDto(
                null,
                body.chambreDto().matricule(),
                body.chambreDto().capacite(),
                body.chambreDto().etat(),
                null,
                null,
                null,
                etageDto.id()));

    UtilisateurDto utilisateurDto =
        utilisateurService.save(
            new UtilisateurDto(
                null,
                body.utilisateurDto().nom(),
                body.utilisateurDto().prenom(),
                body.utilisateurDto().cin(),
                body.utilisateurDto().adresse(),
                body.utilisateurDto().telephone(),
                null,
                null,
                null,
                null,
                null,
                null));

    ServiceDto serviceDto =
        serviceService.save(new ServiceDto(null, body.serviceDto().nom(), null));

    ReclamationDto reclamationDto =
        reclamationService.save(
            new ReclamationDto(
                null,
                body.reclamationDto().message(),
                null,
                utilisateurDto.id(),
                chambreDto.id(),
                serviceDto.id(),
                null,
                null));

    EquipementDto equipementDto =
        equipementService.save(new EquipementDto(null, body.equipementDto().nom(), null, null));

    EquipementReclamationDto dto =
        equipementReclamationService.save(
            new EquipementReclamationDto(null, 2L, equipementDto.id(), reclamationDto.id()));

    return ResponseEntity.ok(dto);
  }
}
