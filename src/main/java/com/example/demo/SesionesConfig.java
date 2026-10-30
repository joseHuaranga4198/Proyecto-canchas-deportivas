package com.example.demo;

import java.util.Collections;
import jakarta.servlet.SessionTrackingMode;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Cada navegación conserva su propia sesión sin compartir una cookie entre pestañas. */
@Configuration
public class SesionesConfig {
    @Bean
    public ServletContextInitializer sesionesPorUrl() {
        return contexto -> contexto.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.URL));
    }
}
