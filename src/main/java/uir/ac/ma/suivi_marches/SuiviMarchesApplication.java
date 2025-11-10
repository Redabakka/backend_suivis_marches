package uir.ac.ma.suivi_marches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuiviMarchesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuiviMarchesApplication.class, args);
    }

}

// IMPORTANT: There is a documentation of every endpoint with swagger.
// Use the URL to access it after running the app: http://localhost:8080/swagger-ui/index.html
// Use the port that you're using, in my case is 8080 (as in application.properties)