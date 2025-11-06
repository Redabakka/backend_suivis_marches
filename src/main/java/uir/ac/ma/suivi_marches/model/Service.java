package uir.ac.ma.suivi_marches.model;

public class Service {
    private int id_service;
    private String nom;
    private String description;
    private boolean actif;

    public Service() {}

    public Service(int id_service, String nom, String description, boolean actif) {
        this.id_service = id_service;
        this.nom = nom;
        this.description = description;
        this.actif = actif;
    }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
