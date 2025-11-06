package uir.ac.ma.suivi_marches.repository;

import uir.ac.ma.suivi_marches.model.Employe;

import java.util.List;

public interface EmployeRepo {
    List<Employe> getAllEmploye();

    Employe getEmployeById(int idEmploye);

    void addEmploye(Employe employe);

    void modifyEmploye(Employe employe);

    void deleteEmploye(int idEmploye);

}