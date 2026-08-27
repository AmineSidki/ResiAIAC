package org.aminesidki.resiaiac.repository;

import java.util.List;
import java.util.Optional;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurPromotionChambreRepository
    extends JpaRepository<UtilisateurPromotionChambre, UtilisateurPromotionChambreId> {
  Optional<UtilisateurPromotionChambre> findTopByUtilisateurOrderByPromotion_AnneeDeFinDesc(
      Utilisateur utilisateur);

  List<UtilisateurPromotionChambre> findAllByUtilisateur(Utilisateur utilisateur);

  List<UtilisateurPromotionChambre> findAllByChambre(Chambre chambre);
}
