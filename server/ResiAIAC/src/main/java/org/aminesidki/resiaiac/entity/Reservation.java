package org.aminesidki.resiaiac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.aminesidki.resiaiac.enumeration.EtatReservation;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue
    private UUID id;

    private EtatReservation etat;

    @ManyToOne
    @JoinColumn(name = "utilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "chambre")
    private Chambre chambre;

    @CreationTimestamp
    private Timestamp createdAt;
}
