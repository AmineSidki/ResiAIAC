package org.aminesidki.resiaiac.repository;

import java.util.UUID;
import org.aminesidki.resiaiac.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, UUID> {}
