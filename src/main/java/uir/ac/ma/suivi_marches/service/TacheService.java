package uir.ac.ma.suivi_marches.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Tache;
import uir.ac.ma.suivi_marches.repository.TacheRepo;

import java.util.List;
import java.util.Optional;

@Service
public class TacheService {
    TacheRepo tacheRepo;

    @Autowired
    public TacheService(TacheRepo tacheRepo) {
        this.tacheRepo = tacheRepo;
    }

    public List<Tache> getAllTaches() {
        return tacheRepo.findAll();
    }

    public Optional<Tache> getTacheById(int idTache) {
        return tacheRepo.findById(idTache);
    }

    @Transactional
    public Tache addTache(Tache tache) {
        return tacheRepo.save(tache);
    }

    @Transactional
    public Tache modifyTache(Tache tache) {
        return tacheRepo.save(tache);
    }

    @Transactional
    public void deleteTache(int idTache) {
        tacheRepo.deleteById(idTache);
    }
}