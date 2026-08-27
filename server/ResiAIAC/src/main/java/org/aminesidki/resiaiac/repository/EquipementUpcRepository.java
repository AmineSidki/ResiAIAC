package org.aminesidki.resiaiac.repository;

import java.util.List;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementUpc;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.EquipementUpcId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementUpcRepository extends JpaRepository<EquipementUpc, EquipementUpcId> {
  List<EquipementUpc> findAllByUpc(UtilisateurPromotionChambre entity);

  Page<EquipementUpc> findAllByEquipement(Equipement equipement, Pageable pageable);
}
