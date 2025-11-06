package uir.ac.ma.suivi_marches.Model;

import java.time.LocalDateTime;

public class Employe {
    private int id_employe;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private int id_service;      // référence vers la table service
    private boolean actif;
    private LocalDateTime created_at;

    public Employe() {}

    public Employe(int id_employe, String nom, String prenom, String email, String role,
                   int id_service, boolean actif, LocalDateTime created_at) {
        this.id_employe = id_employe;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.id_service = id_service;
        this.actif = actif;
        this.created_at = created_at;
    }

    // Getters et Setters
    public int getId_employe() { return id_employe; }
    public void setId_employe(int id_employe) { this.id_employe = id_employe; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

}
