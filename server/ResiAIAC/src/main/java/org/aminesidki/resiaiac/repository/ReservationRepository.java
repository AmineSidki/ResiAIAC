package org.aminesidki.resiaiac.repository;

import java.util.List;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Reservation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
  Page<Reservation> findAllByUtilisateur(Utilisateur utilisateur, Pageable pageable);

  List<Reservation> findAllByUtilisateurAndEtat(Utilisateur utilisateur, EtatReservation etat);
}
