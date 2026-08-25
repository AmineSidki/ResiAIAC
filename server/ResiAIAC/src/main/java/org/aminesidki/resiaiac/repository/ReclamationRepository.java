package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReclamationRepository extends JpaRepository<Reclamation, UUID> {
  Page<Reclamation> findAllByUtilisateur(Utilisateur utilisateur, Pageable pageable);

  Page<Reclamation> findAllByUtilisateurAndEtat(
      Utilisateur utilisateur, EtatReclamation etat, Pageable pageable);

  Page<Reclamation> findAllByEtat(EtatReclamation etat, Pageable pageable);
}
