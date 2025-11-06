package uir.ac.ma.suivi_marches.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
public class Marche {
    private int id_marche;
    private String intitule;
    private String objectif;
    private double budget_estime;
    private LocalDate date_debut;
    private LocalDate date_fin;
    private String statut;
    private int id_service;           // clé étrangère vers service
    private String fichier_cps_path;  // chemin du fichier CPS (.txt)
    private int created_by;           // clé étrangère vers employe
    private LocalDateTime created_at;

    public Marche() {}

    public Marche(int id_marche, String intitule, String objectif, double budget_estime,
                  LocalDate date_debut, LocalDate date_fin, String statut,
                  int id_service, String fichier_cps_path, int created_by, LocalDateTime created_at) {
        this.id_marche = id_marche;
        this.intitule = intitule;
        this.objectif = objectif;
        this.budget_estime = budget_estime;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.statut = statut;
        this.id_service = id_service;
        this.fichier_cps_path = fichier_cps_path;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    // Getters et Setters
    public int getId_marche() { return id_marche; }
    public void setId_marche(int id_marche) { this.id_marche = id_marche; }

    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public double getBudget_estime() { return budget_estime; }
    public void setBudget_estime(double budget_estime) { this.budget_estime = budget_estime; }

    public LocalDate getDate_debut() { return date_debut; }
    public void setDate_debut(LocalDate date_debut) { this.date_debut = date_debut; }

    public LocalDate getDate_fin() { return date_fin; }
    public void setDate_fin(LocalDate date_fin) { this.date_fin = date_fin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }

    public String getFichier_cps_path() { return fichier_cps_path; }
    public void setFichier_cps_path(String fichier_cps_path) { this.fichier_cps_path = fichier_cps_path; }

    public int getCreated_by() { return created_by; }
    public void setCreated_by(int created_by) { this.created_by = created_by; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

}
