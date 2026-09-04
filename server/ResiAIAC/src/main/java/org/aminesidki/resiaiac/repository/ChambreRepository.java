package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Chambre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChambreRepository extends JpaRepository<Chambre, UUID> {
  @Query(value = "SELECT c from Chambre c  where c.etat = 'LIBRE' ORDER BY FUNCTION('RANDOM')")
  Chambre getRandomChambre(Pageable pageable);
}
