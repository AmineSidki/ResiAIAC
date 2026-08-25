package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
  Page<Document> findAllByProprietaire(Utilisateur proprietaire, Pageable pageable);

  Page<Document> findAllBy(Pageable pageable);

  Document findFirstByNomSceauAndProprietaire(String nomSceau, Utilisateur proprietaire);

  Page<Document> findAllByEtat(EtatDocument etat, Pageable pageable);

  Page<Document> getAllByProprietaireAndEtat(
      Utilisateur proprietaire, EtatDocument etat, Pageable pageable);
}
