package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {}
