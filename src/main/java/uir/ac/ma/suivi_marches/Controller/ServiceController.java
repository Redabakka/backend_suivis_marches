package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.ServiceService;
import uir.ac.ma.suivi_marches.model.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
@CrossOrigin
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // 🔹 Récupérer tous les services
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // 🔹 Récupérer un service par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable("id") int idService) {
        Optional<Service> service = serviceService.getServiceById(idService);

        if (service.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        return ResponseEntity.ok(service.get());
    }

    // 🔹 Ajouter un nouveau service
    @PostMapping
    public ResponseEntity<?> addService(@RequestBody Map<String, Object> request) {
        try {
            String nom = request.get("nom") != null ? request.get("nom").toString() : null;
            String description = request.containsKey("description")
                    ? request.get("description").toString()
                    : null;
            boolean actif = !request.containsKey("actif")
                    || Boolean.parseBoolean(request.get("actif").toString());

            // Validation
            if (nom == null || nom.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Le nom du service est obligatoire"));
            }

            // DB : VARCHAR(100)
            if (nom.length() > 100) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Le nom ne doit pas dépasser 100 caractères"));
            }

            // Règle métier : description max 500 (tu peux enlever si tu veux illimité)
            if (description != null && description.length() > 500) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "La description ne doit pas dépasser 500 caractères"));
            }

            Service service = new Service();
            service.setNom(nom.trim());
            service.setDescription(description);
            service.setActif(actif);

            Service savedService = serviceService.addService(service);

            return ResponseEntity.ok(Map.of(
                    "message", "Service créé avec succès",
                    "id_service", savedService.getId_service()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la création: " + e.getMessage())
            );
        }
    }

    // 🔹 Modifier un service existant
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyService(@PathVariable("id") int idService,
                                           @RequestBody Map<String, Object> request) {
        Optional<Service> existingService = serviceService.getServiceById(idService);

        if (existingService.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        try {
            Service service = existingService.get();

            if (request.containsKey("nom")) {
                String nom = request.get("nom") != null ? request.get("nom").toString() : null;
                if (nom == null || nom.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Le nom du service ne peut pas être vide"));
                }
                if (nom.length() > 100) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Le nom ne doit pas dépasser 100 caractères"));
                }
                service.setNom(nom.trim());
            }

            if (request.containsKey("description")) {
                String description = request.get("description") != null
                        ? request.get("description").toString()
                        : null;
                if (description != null && description.length() > 500) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "La description ne doit pas dépasser 500 caractères"));
                }
                service.setDescription(description);
            }

            if (request.containsKey("actif")) {
                boolean actif = Boolean.parseBoolean(request.get("actif").toString());
                service.setActif(actif);
            }

            Service updatedService = serviceService.modifyService(service);

            return ResponseEntity.ok(Map.of(
                    "message", "Service modifié avec succès",
                    "service", updatedService
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la modification: " + e.getMessage())
            );
        }
    }

    // 🔹 Supprimer (soft delete) : marquer inactif
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable("id") int idService) {
        Optional<Service> service = serviceService.getServiceById(idService);

        if (service.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        try {
            Service existingService = service.get();
            existingService.setActif(false);
            serviceService.modifyService(existingService);

            return ResponseEntity.ok(Map.of("message", "Service désactivé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la désactivation: " + e.getMessage())
            );
        }
    }

    // 🔹 Supprimer définitivement (hard delete)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDeleteService(@PathVariable("id") int idService) {
        Optional<Service> service = serviceService.getServiceById(idService);

        if (service.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        try {
            serviceService.deleteService(idService);
            return ResponseEntity.ok(Map.of("message", "Service supprimé définitivement"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la suppression: " + e.getMessage())
            );
        }
    }

    // 🔹 Services actifs
    @GetMapping("/actifs")
    public ResponseEntity<List<Service>> getActiveServices() {
        List<Service> services = serviceService.getAllServices()
                .stream()
                .filter(Service::isActif)
                .toList();
        return ResponseEntity.ok(services);
    }
}
