-- PostgreSQL schema for Suivi des Marchés
-- Drop order (for dev only)
-- DROP TABLE IF EXISTS notification, signalement, approbation, commentaire, tache, marche, employe, service CASCADE;


-- Table unique d'authentification + rôle (simplifiée)
CREATE TABLE IF NOT EXISTS utilisateur (
                                           id_user SERIAL PRIMARY KEY,
                                           id_employe INT UNIQUE REFERENCES employe(id_employe)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('Admin','Chef','Employe'))
    );

-- Index utiles
CREATE INDEX IF NOT EXISTS idx_user_username ON utilisateur(username);
CREATE INDEX IF NOT EXISTS idx_user_role ON utilisateur(role);


CREATE TABLE service (
                         id_service SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL,
                         description TEXT,
                         actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE employe (
                         id_employe SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL,
                         prenom VARCHAR(100) NOT NULL,
                         email VARCHAR(150) UNIQUE NOT NULL,
                         role VARCHAR(20) NOT NULL CHECK (role IN ('Employe','Chef','Admin')),
                         id_service INT NOT NULL REFERENCES service(id_service) ON UPDATE CASCADE ON DELETE RESTRICT,
                         actif BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE marche (
                        id_marche SERIAL PRIMARY KEY,
                        intitule VARCHAR(200) NOT NULL,
                        objectif TEXT NOT NULL,
                        budget_estime DECIMAL(12,2) CHECK (budget_estime >= 0),
                        date_debut DATE NOT NULL,
                        date_fin DATE NOT NULL,
                        statut VARCHAR(30) NOT NULL CHECK (statut IN ('Brouillon','En attente approbations','Approuvé par service','Validé chef','Refusé')),
                        id_service INT NOT NULL REFERENCES service(id_service) ON UPDATE CASCADE ON DELETE RESTRICT,
                        fichier_cps_path VARCHAR(255) NOT NULL,
                        created_by INT NOT NULL REFERENCES employe(id_employe) ON UPDATE CASCADE ON DELETE RESTRICT,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        CHECK (date_fin >= date_debut)
);

CREATE INDEX idx_marche_service ON marche(id_service);
CREATE INDEX idx_marche_dates ON marche(date_debut, date_fin);

CREATE TABLE tache (
                       id_tache SERIAL PRIMARY KEY,
                       id_marche INT NOT NULL REFERENCES marche(id_marche) ON UPDATE CASCADE ON DELETE CASCADE,
                       titre VARCHAR(200) NOT NULL,
                       description TEXT,
                       date_debut DATE NOT NULL,
                       date_fin DATE NOT NULL,
                       duree_estimee INT CHECK (duree_estimee IS NULL OR duree_estimee >= 0),
                       responsable INT NOT NULL REFERENCES employe(id_employe) ON UPDATE CASCADE ON DELETE RESTRICT,
                       etat VARCHAR(30) NOT NULL CHECK (etat IN ('En attente','En cours','Validée','Non validée')),
                       priorite VARCHAR(20) NOT NULL CHECK (priorite IN ('Urgent','Quotidien','Informatif')),
                       critique BOOLEAN NOT NULL DEFAULT FALSE,
                       pertinence VARCHAR(20) CHECK (pertinence IN ('Pertinente','Non pertinente','À revoir')),
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       CHECK (date_fin >= date_debut)
);

CREATE INDEX idx_tache_marche ON tache(id_marche);
CREATE INDEX idx_tache_responsable ON tache(responsable);

CREATE TABLE commentaire (
                             id_commentaire SERIAL PRIMARY KEY,
                             id_tache INT NOT NULL REFERENCES tache(id_tache) ON UPDATE CASCADE ON DELETE CASCADE,
                             id_auteur INT NOT NULL REFERENCES employe(id_employe) ON UPDATE CASCADE ON DELETE RESTRICT,
                             contenu TEXT NOT NULL,
                             priorite VARCHAR(20) CHECK (priorite IN ('Urgent','Quotidien','Informatif')),
                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE approbation (
                             id_approbation SERIAL PRIMARY KEY,
                             id_marche INT NOT NULL REFERENCES marche(id_marche) ON UPDATE CASCADE ON DELETE CASCADE,
                             id_employe INT NOT NULL REFERENCES employe(id_employe) ON UPDATE CASCADE ON DELETE CASCADE,
                             statut VARCHAR(20) NOT NULL CHECK (statut IN ('Approuvé','Refusé')),
                             motif TEXT,
                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                             CONSTRAINT unique_approbation UNIQUE (id_marche, id_employe)
);

CREATE TABLE signalement (
                             id_signalement SERIAL PRIMARY KEY,
                             id_tache INT NOT NULL REFERENCES tache(id_tache) ON UPDATE CASCADE ON DELETE CASCADE,
                             id_employe INT NOT NULL REFERENCES employe(id_employe) ON UPDATE CASCADE ON DELETE CASCADE,
                             type VARCHAR(20) NOT NULL CHECK (type IN ('Validée','Non pertinente')),
                             commentaire TEXT,
                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE notification (
                              id_notification SERIAL PRIMARY KEY,
                              id_employe INT NOT NULL REFERENCES employe(id_employe) ON UPDATE CASCADE ON DELETE CASCADE,
                              type VARCHAR(50) NOT NULL,
                              message TEXT NOT NULL,
                              lu BOOLEAN NOT NULL DEFAULT FALSE,
                              date_envoi TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Helpful views (optional)
-- View counting approvals for a market
CREATE OR REPLACE VIEW v_approbations_par_marche AS
SELECT m.id_marche,
       COUNT(a.*) FILTER (WHERE a.statut = 'Approuvé') AS approuves,
    COUNT(a.*) FILTER (WHERE a.statut = 'Refusé') AS refuses,
    COUNT(a.*) AS total_votes
FROM marche m
         LEFT JOIN approbation a ON a.id_marche = m.id_marche
GROUP BY m.id_marche;
