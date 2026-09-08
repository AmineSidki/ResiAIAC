package org.aminesidki.resiaiac.service;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.dto.UtilisateurPromotionChambreDto;
import org.aminesidki.resiaiac.dto.request.RoomAssignationRequest;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;

public interface UtilisateurPromotionChambreService {
  UtilisateurPromotionChambreDto assignRoom(RoomAssignationRequest request);

  List<UtilisateurPromotionChambreDto> getAllByChambreId(UUID id);

  List<UtilisateurPromotionChambreDto> getAllByUserId(UUID id);

  UtilisateurPromotionChambre getEntityById(UtilisateurPromotionChambreId id);

  Chambre getCurrentRoomByUser(Utilisateur utilisateur);

  UtilisateurPromotionChambreDto save(UtilisateurPromotionChambreDto dto);

  UtilisateurPromotionChambreDto getById(UUID utilisateurId, UUID promotionId, UUID chambreId);

  UtilisateurPromotionChambreDto update(
      UtilisateurPromotionChambreId id, UtilisateurPromotionChambreDto dto);

  void delete(UUID utilisateurId, UUID promotionId, UUID chambreId);
}
