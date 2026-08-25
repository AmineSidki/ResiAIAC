package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
  Page<Promotion> findAllBy(Pageable pageable);

  Page<Promotion> findAllByFiliere(Filiere filiere, Pageable pageable);
}
