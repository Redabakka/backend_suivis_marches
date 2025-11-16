-----> Pour tester le backend, vous devez suivre la structure suivante:
0-Pré-requis
    Ton backend tourne sur http://localhost:8080
    Tous tes scripts SQL (CREATE TABLE…) sont déjà exécutés
    Tu as bien recompilé après tes changements de modèles / controllers
--->on commance par
1-Créer un Service
        POST /api/services
        exemple
        {
        "nom": "Informatique",
        "description": "Service IT",
        "actif": true
        }
2-Créer un Employé
        Un employé a besoin d’un service (id_service).
        POST /api/employes
        exemples:
        {
        "nom": "Dupont",
        "prenom": "Jean",
        "email": "jean.dupont@example.com",
        "role": "ADMIN",              // ou CHEF, EMPLOYE
        "idService": 1,               // ID du service créé à l'étape 1
        "actif": true
        }

3-Créer un Utilisateur lié à l’employé
        POST /api/auth/register
        exemple pour le registre
        {
        "username": "admin",
        "password": "admin123",
        "role": "ADMIN",
        "idEmploye": 1        // ID de l'employé créé à l'étape 2
        }
        exemple pour le login
        {
        "username": "admin",
        "password": "admin123"
        }

4-Créer un Marché
        Un marché dépend de :
        Service (idService)
        Employe créateur (idCreatedBy)
        POST /api/marches
        exemple:
        {
        "intitule": "Marché de fourniture informatique",
        "objectif": "Renouvellement du parc PC",
        "budgetEstime": 500000,
        "dateDebut": "2025-01-01",
        "dateFin": "2025-12-31",
        "statut": "EN_PREPARATION",
        "idService": 1,                 // service créé à l'étape 1
        "fichierCpsPath": "/docs/cps1.pdf",
        "idCreatedBy": 1                // employé créé à l'étape 2
        }

5-Créer une Tâche
        Une tâche dépend de :
        Marche (idMarche)
        Employe responsable (responsable)
        POST /api/taches
        exemple:
        {
        "idMarche": 1,                   // marché créé à l'étape 4
        "titre": "Préparer le cahier des charges",
        "description": "Collecter les besoins des services",
        "dateDebut": "2025-02-01",
        "dateFin": "2025-02-15",
        "dureeEstimee": 10,
        "responsable": 1,                // id_employe
        "etat": "En attente",
        "priorite": "Urgent",
        "critique": true,
        "pertinence": "Pertinente"
        }

6-Créer un Commentaire sur une tâche
        Dépend de :
        Tache (idTache)
        Employe auteur (idAuteur)
        POST /api/commentaires
        exemple:
        {
        "idTache": 1,
        "idAuteur": 1,
        "contenu": "Merci de valider ce périmètre au plus vite.",
        "priorite": "URGENT"   // selon ton enum dans Commentaire
        }

7-Créer une Approbation sur un marché
        Dépend de :
        Marche (idMarche)
        Employe (idEmploye)
        Contrainte unique (id_marche, id_employe)
        POST /api/approbations
        exemple:
        {
        "idMarche": 1,
        "idEmploye": 1,
        "statut": "APPROUVE",        // ton contrôleur mappe vers 'Approuvé' pour la DB
        "motif": "Conforme aux besoins."
        }

8-Créer un Signalement sur une tâche
        Dépend de :
        Tache
        Employe
        POST /api/signalements
        exemple:
        {
        "idTache": 1,
        "idEmploye": 1,
        "type": "VALIDEE",           // ton contrôleur doit mapper vers "Validée"
        "commentaire": "Tâche réalisée correctement."
        }

9-Créer une Notification
        Dépend uniquement de :
        Employe (idEmploye)
        POST /api/notifications
        exemple:
        {
        "idEmploye": 1,
        "type": "INFO",               // selon ton enum Notification.Type
        "message": "Une nouvelle tâche vous a été assignée",
        "lu": false
        }

et maintenant, si tout se passe bien, on est à la fin, le backend est fonctionnel












