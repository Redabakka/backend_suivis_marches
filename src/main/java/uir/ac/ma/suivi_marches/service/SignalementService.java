package uir.ac.ma.suivi_marches.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Signalement;
import uir.ac.ma.suivi_marches.Repository.SignalementRepo;

import java.util.List;
import java.util.Optional;

@Service
public class SignalementService {
    SignalementRepo signalementRepo;

    @Autowired
    public SignalementService(SignalementRepo signalementRepo) {
        this.signalementRepo = signalementRepo;
    }

    public List<Signalement> getAllSignalements() {
        return signalementRepo.findAll();
    }

    public Optional<Signalement> getSignalementById(int idSignalement) {
        return signalementRepo.findById(idSignalement);
    }

    @Transactional
    public Signalement addSignalement(Signalement signalement) {
        return signalementRepo.save(signalement);
    }

    @Transactional
    public Signalement modifySignalement(Signalement signalement) {
        return signalementRepo.save(signalement);
    }

    @Transactional
    public void deleteSignalement(int idSignalement) {
        signalementRepo.deleteById(idSignalement);
    }
}