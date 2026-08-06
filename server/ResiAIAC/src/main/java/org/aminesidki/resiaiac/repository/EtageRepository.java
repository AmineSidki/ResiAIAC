package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Etage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtageRepository extends JpaRepository<Etage, UUID> {}
