package uir.ac.ma.suivi_marches.repository;

import uir.ac.ma.suivi_marches.model.Marche;

import java.util.List;

public interface MarcheRepo {
    List<Marche> getAllMarche();

    Marche getMarcheById(int idMarche);

    void addMarche(Marche marche);

    void modifyMarche(Marche marche);

    void deleteMarche(int idMarche);

}