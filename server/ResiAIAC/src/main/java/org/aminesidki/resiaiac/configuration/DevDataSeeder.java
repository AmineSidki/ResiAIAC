package org.aminesidki.resiaiac.configuration;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.entity.Chambre;
import org.aminesidki.resiaiac.entity.Equipement;
import org.aminesidki.resiaiac.entity.Etage;
import org.aminesidki.resiaiac.entity.Filiere;
import org.aminesidki.resiaiac.entity.Promotion;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.entity.Utilisateur;
import org.aminesidki.resiaiac.entity.UtilisateurPromotionChambre;
import org.aminesidki.resiaiac.entity.id.UtilisateurPromotionChambreId;
import org.aminesidki.resiaiac.enumeration.EtatChambre;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.aminesidki.resiaiac.repository.ChambreRepository;
import org.aminesidki.resiaiac.repository.EquipementRepository;
import org.aminesidki.resiaiac.repository.EtageRepository;
import org.aminesidki.resiaiac.repository.FiliereRepository;
import org.aminesidki.resiaiac.repository.PromotionRepository;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.repository.UtilisateurPromotionChambreRepository;
import org.aminesidki.resiaiac.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Local dev/demo data. Only runs when the "seed" profile is active (`mvn spring-boot:run
 * -Dspring-boot.run.profiles=seed`, or add SPRING_PROFILES_ACTIVE=seed to the `application` service
 * in compose.yaml), so it never touches a real deployment. Idempotent — checks for existing rows
 * and does nothing if any are found, so restarting the app with this profile repeatedly doesn't
 * duplicate data.
 *
 * <p>The two Utilisateur rows below are linked by keycloakUser to the defaultadmin/etudiant
 * accounts already defined in keycloak/import/ResiAIAC-realm.json. Without these rows, logging in
 * as either account fails with a 404 on /me — UtilisateurServiceImpl.getMyEntityByJwt has no
 * auto-provisioning, it just throws ResourceNotFoundException if no matching row exists.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DevDataSeeder {

  // Fixed ids of the two users already defined in ResiAIAC-realm.json.
  private static final UUID DEFAULT_ADMIN_KEYCLOAK_ID =
      UUID.fromString("5054ad2f-6477-4bcd-93d4-2e76b61f5d6f");
  private static final UUID ETUDIANT_KEYCLOAK_ID =
      UUID.fromString("83085e27-7bc8-4afd-bf8b-76c585fcd3b4");

  @Bean
  CommandLineRunner seedDevData(
      BatimentRepository batimentRepository,
      EtageRepository etageRepository,
      ChambreRepository chambreRepository,
      FiliereRepository filiereRepository,
      PromotionRepository promotionRepository,
      ServiceRepository serviceRepository,
      EquipementRepository equipementRepository,
      UtilisateurRepository utilisateurRepository,
      UtilisateurPromotionChambreRepository utilisateurPromotionChambreRepository) {
    return args -> {
      if (batimentRepository.count() > 0) {
        log.info("DevDataSeeder: data already present, skipping seed.");
        return;
      }
      log.info("DevDataSeeder: seeding local dev/demo data...");

      // --- Batiments / Etages / Chambres -----------------------------------
      Batiment batimentA = batimentRepository.save(Batiment.builder().nom("Bâtiment A").build());
      Batiment batimentB = batimentRepository.save(Batiment.builder().nom("Bâtiment B").build());

      Etage a1 = etageRepository.save(Etage.builder().numero("1").batiment(batimentA).build());
      Etage a2 = etageRepository.save(Etage.builder().numero("2").batiment(batimentA).build());
      Etage b1 = etageRepository.save(Etage.builder().numero("1").batiment(batimentB).build());

      Chambre chambreA101 =
          chambreRepository.save(
              Chambre.builder()
                  .matricule("A-101")
                  .capacite(2L)
                  .etat(EtatChambre.PARTIELLEMENT_LIBRE)
                  .etage(a1)
                  .build());
      chambreRepository.save(
          Chambre.builder()
              .matricule("A-102")
              .capacite(2L)
              .etat(EtatChambre.LIBRE)
              .etage(a1)
              .build());
      chambreRepository.save(
          Chambre.builder()
              .matricule("A-201")
              .capacite(3L)
              .etat(EtatChambre.OCCUPEE)
              .etage(a2)
              .build());
      chambreRepository.save(
          Chambre.builder()
              .matricule("B-101")
              .capacite(2L)
              .etat(EtatChambre.MAINTENANCE)
              .etage(b1)
              .build());

      // --- Filieres / Promotions --------------------------------------------
      Filiere gi =
          filiereRepository.save(
              Filiere.builder().nom("Génie Informatique").niveauMaximal(3).build());
      filiereRepository.save(Filiere.builder().nom("Génie Civil").niveauMaximal(3).build());

      Promotion giPromo1 =
          promotionRepository.save(
              Promotion.builder()
                  .filiere(gi)
                  .anneeDeDepart(2023L)
                  .anneeDeFin(2026L)
                  .niveau(3)
                  .build());

      // --- Services / Equipements ---------------------------------------------
      serviceRepository.save(Service.builder().nom("Maintenance").build());
      serviceRepository.save(Service.builder().nom("Sécurité").build());
      equipementRepository.save(Equipement.builder().nom("Lit").build());
      equipementRepository.save(Equipement.builder().nom("Bureau").build());
      equipementRepository.save(Equipement.builder().nom("Climatiseur").build());

      // --- Utilisateurs (required for login to work — see class javadoc) -----
      Utilisateur admin =
          utilisateurRepository.save(
              Utilisateur.builder()
                  .keycloakUser(DEFAULT_ADMIN_KEYCLOAK_ID)
                  .email("defaultadmin@gmail.com")
                  .nom("Admin")
                  .prenom("Default")
                  .cin("AA000001")
                  .adresse("Casablanca")
                  .telephone("0600000000")
                  .build());

      Utilisateur etudiant =
          utilisateurRepository.save(
              Utilisateur.builder()
                  .keycloakUser(ETUDIANT_KEYCLOAK_ID)
                  .email("etudiant@gmail.com")
                  .nom("Etudiant")
                  .prenom("Test")
                  .cin("BB000002")
                  .adresse("Settat")
                  .telephone("0600000001")
                  .build());

      // Assign the student to a room/promotion so the self-service pages
      // (reservation/reclamation) have something to show against.
      utilisateurPromotionChambreRepository.save(
          UtilisateurPromotionChambre.builder()
              .id(
                  new UtilisateurPromotionChambreId(
                      etudiant.getId(), giPromo1.getId(), chambreA101.getId()))
              .utilisateur(etudiant)
              .promotion(giPromo1)
              .chambre(chambreA101)
              .retard(false)
              .note("Seeded via DevDataSeeder")
              .build());

      log.info(
          "DevDataSeeder: done. Log in as defaultadmin/etudiant (see ResiAIAC-realm.json for passwords).");
    };
  }
}
