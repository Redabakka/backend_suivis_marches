package uir.ac.ma.suivi_marches.repository;


import uir.ac.ma.suivi_marches.model.Service;

import java.util.List;

public interface ServiceRepo {
    List<Service> getAllService();

    Service getServiceById(int idService);

    void addService(Service service);

    void modifyService(Service service);

    void deleteService(int idService);

}