package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Chambre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChambreRepository extends JpaRepository<Chambre, UUID> {
  @Query(
          value = "SELECT * FROM chambre c WHERE c.etat = 'LIBRE' OR c.etat = 'PARTIELLEMENT_LIBRE' ORDER BY RANDOM()",
          countQuery = "SELECT count(*) FROM chambre c WHERE c.etat = 'LIBRE' OR c.etat = 'PARTIELLEMENT_LIBRE'",
          nativeQuery = true
  )
  Page<Chambre> getRandomChambre(Pageable pageable);
}
