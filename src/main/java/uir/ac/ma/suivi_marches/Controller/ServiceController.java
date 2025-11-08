package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.ServiceService;

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
    public ResponseEntity<List<uir.ac.ma.suivi_marches.model.Service>> getAllServices() {
        List<uir.ac.ma.suivi_marches.model.Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // 🔹 Récupérer un service par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable("id") int idService) {
        Optional<uir.ac.ma.suivi_marches.model.Service> service = serviceService.getServiceById(idService);

        if (service.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        return ResponseEntity.ok(service.get());
    }

    // 🔹 Ajouter un nouveau service
    @PostMapping
    public ResponseEntity<?> addService(@RequestBody Map<String, Object> request) {
        try {
            String nom = request.get("nom").toString();
            String description = request.containsKey("description") ?
                    request.get("description").toString() : null;
            boolean actif = !request.containsKey("actif") || Boolean.parseBoolean(request.get("actif").toString());

            // Validation
            if (nom == null || nom.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le nom du service est obligatoire"));
            }

            if (nom.length() > 150) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le nom ne doit pas dépasser 150 caractères"));
            }

            if (description != null && description.length() > 500) {
                return ResponseEntity.badRequest().body(Map.of("message", "La description ne doit pas dépasser 500 caractères"));
            }

            uir.ac.ma.suivi_marches.model.Service service = new uir.ac.ma.suivi_marches.model.Service();
            service.setNom(nom.trim());
            service.setDescription(description);
            service.setActif(actif);

            uir.ac.ma.suivi_marches.model.Service savedService = serviceService.addService(service);

            return ResponseEntity.ok(Map.of(
                    "message", "Service créé avec succès",
                    "id_service", savedService.getId_service()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la création: " + e.getMessage()
            ));
        }
    }

    // 🔹 Modifier un service existant
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyService(@PathVariable("id") int idService,
                                           @RequestBody Map<String, Object> request) {
        Optional<uir.ac.ma.suivi_marches.model.Service> existingService = serviceService.getServiceById(idService);

        if (existingService.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        try {
            uir.ac.ma.suivi_marches.model.Service service = existingService.get();

            // Mettre à jour les champs si présents
            if (request.containsKey("nom")) {
                String nom = request.get("nom").toString();
                if (nom == null || nom.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le nom du service ne peut pas être vide"));
                }
                if (nom.length() > 150) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le nom ne doit pas dépasser 150 caractères"));
                }
                service.setNom(nom.trim());
            }

            if (request.containsKey("description")) {
                String description = request.get("description").toString();
                if (description != null && description.length() > 500) {
                    return ResponseEntity.badRequest().body(Map.of("message", "La description ne doit pas dépasser 500 caractères"));
                }
                service.setDescription(description);
            }

            if (request.containsKey("actif")) {
                boolean actif = Boolean.parseBoolean(request.get("actif").toString());
                service.setActif(actif);
            }

            uir.ac.ma.suivi_marches.model.Service updatedService = serviceService.modifyService(service);

            return ResponseEntity.ok(Map.of(
                    "message", "Service modifié avec succès",
                    "service", updatedService
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la modification: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer un service (soft delete - marquer comme inactif)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable("id") int idService) {
        Optional<uir.ac.ma.suivi_marches.model.Service> service = serviceService.getServiceById(idService);

        if (service.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        try {
            // Soft delete: marquer comme inactif au lieu de supprimer
            uir.ac.ma.suivi_marches.model.Service existingService = service.get();
            existingService.setActif(false);
            serviceService.modifyService(existingService);

            return ResponseEntity.ok(Map.of("message", "Service désactivé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la désactivation: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer définitivement un service (hard delete)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDeleteService(@PathVariable("id") int idService) {
        Optional<uir.ac.ma.suivi_marches.model.Service> service = serviceService.getServiceById(idService);

        if (service.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        try {
            serviceService.deleteService(idService);
            return ResponseEntity.ok(Map.of("message", "Service supprimé définitivement"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer uniquement les services actifs
    @GetMapping("/actifs")
    public ResponseEntity<List<uir.ac.ma.suivi_marches.model.Service>> getActiveServices() {
        List<uir.ac.ma.suivi_marches.model.Service> services = serviceService.getAllServices()
                .stream()
                .filter(uir.ac.ma.suivi_marches.model.Service::isActif)
                .toList();
        return ResponseEntity.ok(services);
    }
}