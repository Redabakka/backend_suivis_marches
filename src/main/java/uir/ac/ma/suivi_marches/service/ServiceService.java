package uir.ac.ma.suivi_marches.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.Repository.ServiceRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {
    ServiceRepo serviceRepo;

    @Autowired
    public ServiceService(ServiceRepo serviceRepo) {
        this.serviceRepo = serviceRepo;
    }

    public List<uir.ac.ma.suivi_marches.model.Service> getAllServices() {
        return serviceRepo.findAll();
    }

    public Optional<uir.ac.ma.suivi_marches.model.Service> getServiceById(int idService) {
        return serviceRepo.findById(idService);
    }

    @Transactional
    public uir.ac.ma.suivi_marches.model.Service addService(uir.ac.ma.suivi_marches.model.Service service) {
        return serviceRepo.save(service);
    }

    @Transactional
    public uir.ac.ma.suivi_marches.model.Service modifyService(uir.ac.ma.suivi_marches.model.Service service) {
        return serviceRepo.save(service);
    }

    @Transactional
    public void deleteService(int idService) {
        serviceRepo.deleteById(idService);
    }
}