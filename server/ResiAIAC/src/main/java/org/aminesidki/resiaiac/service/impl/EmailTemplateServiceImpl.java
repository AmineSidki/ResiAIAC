package org.aminesidki.resiaiac.service.impl;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Document;
import org.aminesidki.resiaiac.entity.Reclamation;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.enumeration.EtatDocument;
import org.aminesidki.resiaiac.enumeration.EtatReclamation;
import org.aminesidki.resiaiac.service.EmailService;
import org.aminesidki.resiaiac.service.EmailTemplateService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

  private final TemplateEngine templateEngine;
  private final EmailService emailService;

  /** Display info for a single état: what the status badge shows and the body message. */
  private record StatutAffichage(String libelle, String couleur, String message) {}

  private StatutAffichage decrireEtatReclamation(EtatReclamation etat) {
    return switch (etat) {
      case EN_ATTENTE ->
          new StatutAffichage(
              "En attente", "#b06000", "Votre réclamation est en attente de traitement.");
      case EN_TRAITEMENT ->
          new StatutAffichage(
              "En traitement",
              "#1a73e8",
              "Un service a été assigné à votre réclamation et interviendra prochainement.");
      case FERME_TRAITE ->
          new StatutAffichage(
              "Fermée — traitée",
              "#188038",
              "Votre réclamation a été traitée avec succès et est désormais clôturée.");
      case FERME_SANS_TRAITEMENT ->
          new StatutAffichage(
              "Fermée — sans traitement",
              "#c5221f",
              "Votre réclamation a été clôturée sans suite.");
    };
  }

  private StatutAffichage decrireEtatDocument(EtatDocument etat) {
    return switch (etat) {
      case AUCUN ->
          new StatutAffichage(
              "Aucune validation requise",
              "#5f6368",
              "Ce document ne nécessite pas de validation.");
      case EN_ATTENTE ->
          new StatutAffichage(
              "En attente de vérification",
              "#b06000",
              "Votre document a bien été reçu et est en attente de vérification.");
      case VALIDE -> new StatutAffichage("Validé", "#188038", "Votre document a été validé.");
      case INVALIDE ->
          new StatutAffichage(
              "Invalidé",
              "#c5221f",
              "Votre document a été invalidé, veuillez le soumettre à nouveau.");
    };
  }

  @Override
  public void envoyerReservationCreee(Utilisateur destinataire, Chambre chambre) {
    Context context = new Context();
    context.setVariable("prenom", destinataire.getPrenom());
    context.setVariable("matricule", chambre.getMatricule());
    String html = templateEngine.process("email/reservation-creee", context);
    emailService.envoyerEmailHtml(destinataire.getEmail(), "Confirmation de réservation", html);
  }

  @Override
  public void envoyerReclamationCreee(Utilisateur destinataire) {
    Context context = new Context();
    context.setVariable("prenom", destinataire.getPrenom());

    String html = templateEngine.process("email/reclamation-creee", context);
    emailService.envoyerEmailHtml(destinataire.getEmail(), "Réclamation reçue", html);
  }

  @Override
  public void envoyerReclamationStatut(Utilisateur destinataire, Reclamation reclamation) {
    StatutAffichage statut = decrireEtatReclamation(reclamation.getEtat());

    Context context = new Context();
    context.setVariable("prenom", destinataire.getPrenom());
    context.setVariable("statutLibelle", statut.libelle());
    context.setVariable("statutCouleur", statut.couleur());
    context.setVariable("message", statut.message());
    // NOTE: Reclamation n'a pas encore de champ "note admin" en base (seulement le message
    // initial de l'étudiant). On passe null en attendant : la ligne "Note" du template restera
    // simplement masquée (elle est déjà protégée par un th:if). Si vous voulez qu'un
    // gestionnaire puisse laisser un commentaire ici, il faut ajouter un champ (ex.
    // `noteAdministrateur`) à Reclamation + ReclamationDto — dites-le et je l'ajoute.
    context.setVariable("note", (String) null);

    String html = templateEngine.process("email/reclamation-statut", context);
    emailService.envoyerEmailHtml(
        destinataire.getEmail(), "Mise à jour de votre réclamation", html);
  }

  @Override
  public void envoyerDocumentStatut(Utilisateur destinataire, Document document) {
    StatutAffichage statut = decrireEtatDocument(document.getEtat());

    Context context = new Context();
    context.setVariable("prenom", destinataire.getPrenom());
    context.setVariable("statutLibelle", statut.libelle());
    context.setVariable("statutCouleur", statut.couleur());
    context.setVariable("message", statut.message());
    // Only populated (and only shown by the template) when the document was invalidated.
    context.setVariable("note", document.getNoteSurValidite());

    String html = templateEngine.process("email/document-statut", context);
    emailService.envoyerEmailHtml(destinataire.getEmail(), "Mise à jour de votre document", html);
  }
}
