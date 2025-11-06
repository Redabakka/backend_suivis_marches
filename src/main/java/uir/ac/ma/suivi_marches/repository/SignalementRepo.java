package uir.ac.ma.suivi_marches.repository;


import uir.ac.ma.suivi_marches.model.Signalement;

import java.util.List;

public interface SignalementRepo {
    List<Signalement> getAllSignalement();

    Signalement getSignalementById(int idSignalement);

    void addSignalement(Signalement signalement);

    void modifySignalement(Signalement signalement);

    void deleteSignalement(int idSignalement);
}