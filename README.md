# ResiAIAC — Système de Gestion de l'Internat AIAC
ResiAIAC est une application web conçue pour automatiser et centraliser la gestion globale de la résidence universitaire et de l'internat de l'Académie Internationale Mohammed VI de l'Aviation Civile (AIAC).
## Contexte et Objectifs
La gestion actuelle de l'internat s'appuie sur des cahiers physiques, des feuilles de calcul Excel et des canaux de communication informels (appels, e-mails, groupes de messagerie). Cette fragmentation complexifie la traçabilité des dossiers d'admission et le suivi des demandes d'intervention technique. 
ResiAIAC vise à regrouper toutes ces opérations au sein d'une plateforme unique, accessible aux étudiants pour leurs démarches quotidiennes et aux équipes administratives pour la gestion logistique.
## Fonctionnalités Principales
Le système est structuré autour de plusieurs modules clés :
* **Module A : Admission et Validation des Dossiers** : Dépôt sécurisé des pièces justificatives par les étudiants, examen et validation de ces pièces par les responsables administratifs.
* **Module B : Réservation et Attribution des Chambres** : Suivi en temps réel de la capacité des chambres, réservation directe par l'étudiant, et vue d'ensemble de l'occupation pour l'équipe logistique.
* **Module C : Réclamations et Maintenance** : Signalement direct par les résidents d'anomalies matérielles, suivi du cycle de vie des tickets et notifications par e-mail lors de la prise en charge ou de la résolution.
* **Module D : Gestion des Départs et États des Lieux** : Contrôle des chambres au départ de l'étudiant, enregistrement des éventuels équipements endommagés, et génération automatique d'une lettre de départ officielle.
* **Module E : Administration, RBAC et Audit** : Gestion des profils utilisateurs, contrôle d'accès basé sur des rôles stricts (Étudiant, Responsable, Manager, Administrateur), et journalisation des actions sensibles.
## Pile Technologique
Le projet s'appuie sur les technologies suivantes :
* **Backend** : Java 17, Spring Boot, Spring Data JPA, Hibernate, MapStruct (mapping entité ↔ DTO), Thymeleaf (pour la génération de documents et e-mails).
* **Frontend** : Angular (TypeScript), RxJS, TailwindCSS.
* **Bases de données & Caching** : PostgreSQL (stockage principal), Redis (cache et gestion des sessions).
* **Stockage de fichiers** : SeaweedFS (stockage distribué compatible S3 pour les pièces justificatives).
* **Sécurité & IAM** : Keycloak (OAuth2, OpenID Connect) pour la gestion d'identité et le contrôle d'accès basé sur les rôles.
* **Tests** : JUnit 5, Mockito et AssertJ pour la couche service, MockMvc (`@WebMvcTest`) pour la couche Controller.
## Démarrage Local
Pour exécuter le backend de l'application en environnement de développement, suivez ces étapes.
### Prérequis
* Java Development Kit (JDK) 17
* Apache Maven (ou le wrapper `mvnw` inclus)
* Docker et Docker Compose
### Configuration
Le projet nécessite un fichier `.env` à la racine de `server/ResiAIAC`, non versionné, pour renseigner les identifiants des bases de données et de l'administrateur Keycloak. Copiez le modèle fourni et complétez-le :
```bash
cp .env.example .env
```

### Démarrage du projet (environnement de développement)
* **Backend** : Exécutez la commande suivante dans le répertoire `server/ResiAIAC` (une fois le fichier `.env` renseigné) :
    ```bash
    docker compose up --build
    ```
  Cela démarre l'application (`:8080`), Keycloak (`:8090`), PostgreSQL (`:5432`), sa base dédiée à Keycloak (`:5433`) et Redis (`:6379`).
* **Frontend**: Aucun client n'est en place en ce moment, pas encore d'instructions.

### Tests
Pour exécuter la suite de tests du backend (`server/ResiAIAC`) :
```bash
./mvnw test
```
 
> [!WARNING]
> The backend client's secret is blank, it will be re-generated on volume wipe. When you start the container, watch out from deleting keycloak's when rebuilding the application for tests. On the first startup or if you do accidentally wipe it, it's fine, the container will import the old settings, all you have to do is to copy the regenerated token into Spring once it starts.
