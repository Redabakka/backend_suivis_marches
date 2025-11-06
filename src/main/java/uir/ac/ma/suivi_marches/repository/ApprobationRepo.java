package uir.ac.ma.suivi_marches.repository;

import uir.ac.ma.suivi_marches.model.Approbation;

import java.util.List;

public interface ApprobationRepo {
    List<Approbation> getAllApprobation();

    Approbation getApprobationById(int idApprobation);

    void addApprobation(Approbation approbation);

    void modifyApprobation(Approbation approbation);

    void deleteApprobation(int idApprobation);
}