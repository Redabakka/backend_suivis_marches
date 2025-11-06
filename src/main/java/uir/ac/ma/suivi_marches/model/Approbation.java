package uir.ac.ma.suivi_marches.Model;
import java.time.LocalDateTime;

public class approbation {
    private int id_approbation;
    private int id_marche;       // clé étrangère vers marche
    private int id_employe;      // clé étrangère vers employe
    private String statut;       // Approuvé / Refusé
    private String motif;        // justification si refusé
    private LocalDateTime created_at;

    public approbation() {}

    public approbation(int id_approbation, int id_marche, int id_employe,
                       String statut, String motif, LocalDateTime created_at) {
        this.id_approbation = id_approbation;
        this.id_marche = id_marche;
        this.id_employe = id_employe;
        this.statut = statut;
        this.motif = motif;
        this.created_at = created_at;
    }

    // Getters et Setters
    public int getId_approbation() { return id_approbation; }
    public void setId_approbation(int id_approbation) { this.id_approbation = id_approbation; }

    public int getId_marche() { return id_marche; }
    public void setId_marche(int id_marche) { this.id_marche = id_marche; }

    public int getId_employe() { return id_employe; }
    public void setId_employe(int id_employe) { this.id_employe = id_employe; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }


}
