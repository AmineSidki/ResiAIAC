# ResiAIAC

**Plateforme de gestion de l'internat universitaire de l'AIAC** — dématérialisation des admissions, des réservations de chambres, des réclamations de maintenance et des états des lieux, avec contrôle d'accès basé sur les rôles.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)

## Contexte

À l'Académie Internationale Mohammed VI de l'Aviation Civile (AIAC), la gestion de l'internat reposait jusqu'ici sur des cahiers physiques, des feuilles de calcul Excel et des échanges informels (appels, e-mails, groupes de messagerie). Cette fragmentation rendait le suivi des dossiers d'admission et des demandes d'intervention technique difficile à tracer, tant pour les étudiants que pour l'équipe administrative.

ResiAIAC centralise ces opérations dans une seule plateforme : les étudiants y déposent leurs pièces justificatives, réservent leur chambre et signalent les anomalies matérielles ; l'équipe administrative y valide les dossiers, suit l'occupation des bâtiments et pilote le cycle de vie des réclamations.

## Fonctionnalités

- **Admission** — dépôt sécurisé des pièces justificatives (CIN, diplômes) par les étudiants, examen et validation par les responsables, avec notification par e-mail à chaque changement de statut.
- **Réservation de chambres** — suivi en temps réel de la capacité des bâtiments, étages et chambres ; réservation directe par l'étudiant ; vue d'ensemble de l'occupation pour l'équipe logistique.
- **Réclamations et maintenance** — signalement des anomalies par les résidents, suivi du cycle de vie du ticket (en attente, en traitement, fermé) et notifications e-mail à la création comme à la résolution.
- **États des lieux** — association étudiant / promotion / chambre avec inventaire des équipements et de leur état, pour tracer les dégradations éventuelles à l'entrée comme à la sortie.
- **Administration et RBAC** — gestion des profils, contrôle d'accès par rôles hiérarchiques (`ÉTUDIANT` < `MANAGER` < `RESPONSABLE` < `ADMINISTRATEUR`) délégué à Keycloak, avec propagation des rôles dans les jetons JWT.

Le frontend (Angular) n'est pas encore développé ; l'API REST du backend, elle, est fonctionnelle et testée.

## Stack technique

| Domaine | Technologies |
|---|---|
| Backend | Java 17, Spring Boot, Spring Data JPA / Hibernate, Spring Security (OAuth2 Resource Server), MapStruct, Thymeleaf (e-mails) |
| Frontend *(à venir)* | Angular, TypeScript, RxJS, TailwindCSS |
| Données & cache | PostgreSQL, Redis |
| Stockage de fichiers | SeaweedFS (compatible S3, pour les pièces justificatives) |
| Identité & accès | Keycloak (OAuth2 / OpenID Connect) |
| Tests | JUnit 5, Mockito, AssertJ, MockMvc |
| CI/CD | GitHub Actions (formatage Spotless, couverture de tests JaCoCo, build & push de l'image Docker) |

## Architecture

```
                     ┌────────────┐
        HTTPS  ───►  │   Nginx    │
                     └─────┬──────┘
                 ┌─────────┼──────────┐
                 ▼         ▼          ▼
          ┌────────────┐ ┌────────┐ ┌───────────┐
          │ Application │ │Keycloak│ │ SeaweedFS │
          │ (Spring     │ │        │ │   (S3)    │
          │  Boot)      │ └───┬────┘ └───────────┘
          └──────┬──────┘     │
                 │       ┌────┴─────┐
        ┌────────┼───┐   │ Postgres │
        ▼            ▼   │(Keycloak)│
   ┌─────────┐  ┌───────┐└──────────┘
   │ Postgres│  │ Redis │
   │  (app)  │  │       │
   └─────────┘  └───────┘
```

Nginx route `/api/v1` vers l'application, `/realms` vers Keycloak, et expose SeaweedFS sur un port dédié pour la récupération des fichiers via URLs pré-signées.

## Démarrage local

### Prérequis

- JDK 17
- Apache Maven (ou le wrapper `mvnw` fourni)
- Docker et Docker Compose

### Configuration

Le backend (`server/ResiAIAC`) lit sa configuration via un fichier `.env` non versionné. Copiez le modèle fourni et complétez-le :

```bash
cd server/ResiAIAC
cp .env.example .env
```

### Lancer l'application

```bash
docker compose up --build
```

Cela démarre :

| Service | Port |
|---|---|
| Application (Spring Boot) | `8080` |
| Keycloak | `8090` |
| PostgreSQL (application) | `5432` |
| PostgreSQL (Keycloak) | `5433` |
| Redis | `6379` |
| SeaweedFS (S3 / Filer / Master) | `8333` / `8888` / `9333` |

> [!WARNING]
> Le secret du client Keycloak utilisé par le backend est vide au premier démarrage ; il est régénéré à chaque réinitialisation du volume Keycloak. Après un premier lancement (ou une purge accidentelle du volume), Keycloak réimporte la configuration du realm : il suffit de reporter le secret régénéré dans le `.env` avant de redémarrer l'application.

### Tests

```bash
./mvnw test
```

La suite couvre les couches service (JUnit 5, Mockito, AssertJ) et contrôleur (`@WebMvcTest` / MockMvc), avec un seuil de couverture minimal appliqué en CI via JaCoCo.

## Déploiement

Les manifestes Kubernetes (`server/deployment`) décrivent un déploiement de référence sur cluster (testé sur k3s) : `Deployment` pour l'application, Keycloak et Nginx, `StatefulSet` pour les bases PostgreSQL et SeaweedFS, avec `Secret`s dédiés par service. L'image de l'application est construite et publiée automatiquement sur GHCR via GitHub Actions à chaque push sur `main`.
