package org.aminesidki.resiaiac.repository;

import org.aminesidki.resiaiac.entity.EquipementReclamation;
import org.aminesidki.resiaiac.entity.id.EquipementReclamationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementReclamationRepository
    extends JpaRepository<EquipementReclamation, EquipementReclamationId> {}
