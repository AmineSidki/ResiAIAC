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

* **Backend** : Java 17, Spring Boot, Spring Data JPA, Hibernate, Thymeleaf (pour la génération de documents et e-mails).
* **Frontend** : Angular (TypeScript), RxJS, TailwindCSS.
* **Bases de données & Caching** : PostgreSQL (stockage principal), Redis (cache et gestion des sessions).
* **Stockage de fichiers** : SeaweedFS (stockage distribué compatible S3 pour les pièces justificatives).
* **Sécurité & IAM** : Keycloak (OAuth2, OpenID Connect) pour la gestion d'identité et le contrôle d'accès basé sur les rôles.

## Démarrage Local

Pour exécuter le backend de l'application en environnement de développement, suivez ces étapes.

### Prérequis
* Java Development Kit (JDK) 17
* Apache Maven (ou le wrapper `mvnw` inclus)
* Docker et Docker Compose

### Lancement de la Base de Données
Le conteneur PostgreSQL est défini dans le fichier de configuration Docker Compose situé dans le dossier du serveur.

```bash
cd server/ResiAIAC
docker compose up -d
```

### Lancement de l'Application Spring Boot
Grâce au support d'intégration de Spring Boot Docker Compose, l'application peut se connecter automatiquement au service démarré localement.

Pour compiler et lancer l'API :
```bash
./mvnw clean spring-boot:run
```

L'application compilera le code, démarrera le serveur sur son port par défaut (8080) et créera la structure des tables SQL à partir des entités définies.
