package uir.ac.ma.suivi_marches.repository;

import uir.ac.ma.suivi_marches.model.Tache;

import java.util.List;

public interface TacheRepo {
    List<Tache> getAllTache();

    Tache getTacheById(int idTache);

    void addTache(Tache tache);

    void modifyTache(Tache tache);

    void deleteTache(int idTache);
}