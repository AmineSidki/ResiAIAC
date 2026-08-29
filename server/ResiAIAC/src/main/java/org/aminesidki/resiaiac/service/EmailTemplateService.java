package org.aminesidki.resiaiac.service;

import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Utilisateur;

/**
 * Builds and sends the HTML transactional emails (Thymeleaf templates under templates/email/) for
 * reservations, reclamations and documents.
 *
 * <p>The "statut" methods read the entity's état as it was just persisted, so the badge shown in
 * the email always reflects the real, current state of the resource — never a hardcoded label
 * chosen by the caller.
 */
public interface EmailTemplateService {

  void envoyerReservationCreee(Utilisateur destinataire, Chambre chambre);

  void envoyerReclamationCreee(Utilisateur destinataire);

  void envoyerReclamationStatut(Utilisateur destinataire, Reclamation reclamation);

  void envoyerDocumentStatut(Utilisateur destinataire, Document document);
}
