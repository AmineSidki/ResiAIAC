package org.aminesidki.resiaiac.repository;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementReclamationRepository
    extends JpaRepository<EquipementReclamation, EquipementReclamationId> {
  Page<EquipementReclamation> findAllByEquipement(Equipement equipement, Pageable pageable);

  List<EquipementReclamation> findAllByReclamation_Id(UUID reclamationId);
}
