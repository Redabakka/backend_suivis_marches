package uir.ac.ma.suivi_marches.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Employe;
import uir.ac.ma.suivi_marches.Repository.EmployeRepo;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeService {
    EmployeRepo employeRepo;

    @Autowired
    public EmployeService(EmployeRepo employeRepo) {
        this.employeRepo = employeRepo;
    }

    public List<Employe> getAllEmployes() {
        return employeRepo.findAll();
    }

    public Optional<Employe> getEmployeById(int idEmploye) {
        return employeRepo.findById(idEmploye);
    }

    @Transactional
    public Employe addEmploye(Employe employe) {
        return employeRepo.save(employe);
    }

    @Transactional
    public Employe modifyEmploye(Employe employe) {
        return employeRepo.save(employe);
    }

    @Transactional
    public void deleteEmploye(int idEmploye) {
        employeRepo.deleteById(idEmploye);
    }
}