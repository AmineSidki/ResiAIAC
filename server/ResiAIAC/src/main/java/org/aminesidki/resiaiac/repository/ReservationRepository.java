package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {}
