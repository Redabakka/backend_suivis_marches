package uir.ac.ma.suivi_marches.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Approbation;
import uir.ac.ma.suivi_marches.Repository.ApprobationRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ApprobationService {
    ApprobationRepo approbationRepo;

    @Autowired
    public ApprobationService(ApprobationRepo approbationRepo) {
        this.approbationRepo = approbationRepo;
    }

    public List<Approbation> getAllApprobations() {
        return approbationRepo.findAll();
    }

    public Optional<Approbation> getApprobationById(int idApprobation) {
        return approbationRepo.findById(idApprobation);
    }

    @Transactional
    public Approbation addApprobation(Approbation approbation) {
        return approbationRepo.save(approbation);
    }

    @Transactional
    public Approbation modifyApprobation(Approbation approbation) {
        return approbationRepo.save(approbation);
    }

    @Transactional
    public void deleteApprobation(int idApprobation) {
        approbationRepo.deleteById(idApprobation);
    }
}