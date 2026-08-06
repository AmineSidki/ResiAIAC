package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Batiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatimentRepository extends JpaRepository<Batiment, UUID> {}
