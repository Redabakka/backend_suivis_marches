package uir.ac.ma.suivi_marches.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Marche;
import uir.ac.ma.suivi_marches.Repository.MarcheRepo;

import java.util.List;
import java.util.Optional;

@Service
public class MarcheService {
    MarcheRepo marcheRepo;

    @Autowired
    public MarcheService(MarcheRepo marcheRepo) {
        this.marcheRepo = marcheRepo;
    }

    public List<Marche> getAllMarches() {
        return marcheRepo.findAll();
    }

    public Optional<Marche> getMarcheById(int idMarche) {
        return marcheRepo.findById(idMarche);
    }

    @Transactional
    public Marche addMarche(Marche marche) {
        return marcheRepo.save(marche);
    }

    @Transactional
    public Marche modifyMarche(Marche marche) {
        return marcheRepo.save(marche);
    }

    @Transactional
    public void deleteMarche(int idMarche) {
        marcheRepo.deleteById(idMarche);
    }
}