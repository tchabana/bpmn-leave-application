# LLN — Leave Application BPMN

Projet de démonstration d'une application de gestion de demandes de congés basée sur Camunda BPM et Spring Boot.

## Vue d'ensemble

Ce projet contient un exemple simple d'une application qui orchestre une demande de congé via un processus BPMN.
- Langage : Java 17
- Framework : Spring Boot
- Moteur BPM : Camunda BPM (via starter Spring Boot)
- Base embarquée : H2 (fichier)

L'application expose des endpoints REST pour démarrer un processus de demande de congé, lister et compléter des tâches Camunda. Le processus BPMN (`src/main/resources/process.bpmn`) illustre :
- une tâche utilisateur "Request" (créée pour le demandeur),
- une tâche utilisateur "Approve" (assignée au manager `demo`),
- une gateway pour gérer Approve/Reject/InformationRequired,
- une tâche de service `processLeaveDelegate` pour traiter la demande,
- un timer boundary pour envoyer des rappels via `notificationService` / `notificationDelegate`.

## Arborescence clé

Principaux fichiers et dossiers :

- `pom.xml` — configuration Maven (Java 17, Spring Boot 3.5.5, Camunda 7.24.0 BOM)
- `src/main/java/com/lln/workflow/Application.java` — classe d'entrée Spring Boot + `@EnableProcessApplication`
- `src/main/java/com/lln/workflow/controller/LeaveRequestController.java` — endpoints REST exposés
- `src/main/java/com/lln/workflow/delegate/ProcessLeaveDelegate.java` — logique de traitement de la demande
- `src/main/java/com/lln/workflow/delegate/NotificationDelegate.java` — envoi de rappel (exemple simple)
- `src/main/resources/application.yaml` — configuration (H2, Camunda, déploiement BPMN automatique)
- `src/main/resources/process.bpmn` — diagramme BPMN déployé au démarrage

## Contrat minimal (inputs / outputs)

- Démarrer une demande : POST `/api/leave-requests/start` avec JSON `{ "requester": "alice" }`.
  - Renvoie : `{ "processInstanceId": "...", "message": "Leave request initiated successfully" }`.
- Lister les tâches pour un utilisateur : GET `/api/leave-requests/tasks/{assignee}`
  - Renvoie une liste d'objets `Task` Camunda.
- Compléter une tâche : POST `/api/leave-requests/tasks/{taskId}/complete` avec body JSON contenant les variables de tâche.
  - Renvoie : `{ "message": "Task completed successfully" }`.

## Pré-requis

- Java 17 (compatible avec la configuration du projet)
- Maven 3.6+
- Port libre : 8080

## Installation & exécution

Depuis la racine du projet :

```bash
# Compiler
mvn -DskipTests package

# Lancer l'application
mvn spring-boot:run
```

Ou après `package` vous pouvez exécuter le jar :

```bash
java -jar target/lln-1.0.0-SNAPSHOT.jar
```

L'application démarre sur le port configuré dans `application.yaml` (par défaut `8080`).

## Accéder à Camunda WebApp et H2

- Camunda Webapp (si inclus dans le starter) : http://localhost:8080/app/
  - Utilisateur admin créé dans `application.yaml` : `demo` / `demo`
- Console H2 : http://localhost:8080/h2-console
  - JDBC URL : `jdbc:h2:file:./camunda-h2-database`
  - User : `sa` / `sa`

## Endpoints REST (exemples)

1) Démarrer un processus

```bash
curl -X POST http://localhost:8080/api/leave-requests/start \
  -H "Content-Type: application/json" \
  -d '{"requester":"alice"}'
```

2) Lister les tâches pour un utilisateur (ex: `demo`)

```bash
curl http://localhost:8080/api/leave-requests/tasks/demo
```

3) Compléter une tâche (exemple : approbation)

```bash
curl -X POST http://localhost:8080/api/leave-requests/tasks/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{"approval":"A"}'
```

Remplacez `{taskId}` par l'identifiant réel renvoyé par la liste des tâches.

## Détails du processus BPMN (`process.bpmn`)

- `Process_1` est exécutable et déployé automatiquement (configuration `deployment-resource-pattern` dans `application.yaml`).
- Tâches utilisateurs : `Request` (assignée au `requester`), `Approve` (assignée à `demo`).
- Gateway `Gateway_1kqewfd` : chemins `Approve` -> `Process` (approved), `Reject` -> end event terminate, `InformationRequired` -> back to `Request`.
- Boundary timer sur `Approve` : répète 3 fois tous les 2 jours (R3/P2D). En cas d'expiration, la séquence déclenche la tâche de rappel `Reminder` qui invoque `notificationService` (ou `notificationDelegate`).
- Service task `Process` utilise `processLeaveDelegate` (implémenté dans Java) pour le traitement métier (ex. enregistrement, notifications, mise à jour de variables de processus).

## Composants Java clés

- `LeaveRequestController` : expose les endpoints REST pour démarrer le processus, lister et compléter les tâches (utilise `RuntimeService` et `TaskService`).
- `ProcessLeaveDelegate` : simule le traitement d'une demande (log + set variables `processedDate`, `status`).
- `NotificationDelegate` : exemple de delegate exécutant un rappel (impression dans les logs).

## Configuration importante

Dans `src/main/resources/application.yaml` :
- `spring.datasource` : configuration H2 (fichier local `./camunda-h2-database`).
- `camunda.bpm` : admin user (`demo/demo`), `database.schema-update: true` pour créer/mettre à jour le schéma Camunda à démarrage, `deployment-resource-pattern` pour déployer automatiquement les BPMN trouvés dans le classpath.