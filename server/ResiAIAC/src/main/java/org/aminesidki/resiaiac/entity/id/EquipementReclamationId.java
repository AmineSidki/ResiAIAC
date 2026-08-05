package org.aminesidki.resiaiac.entity.id;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class EquipementReclamationId {
  private Long equipement_id;
  private UUID reclamation_id;
}
