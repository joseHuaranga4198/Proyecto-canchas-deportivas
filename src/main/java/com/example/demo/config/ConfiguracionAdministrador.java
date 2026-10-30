package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Crea el primer administrador solo si aún no existe y se configuraron explícitamente
 * todas las variables de entorno ADMIN_*.
 */
@Component
public class ConfiguracionAdministrador implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionAdministrador.class);

    private final JdbcTemplate jdbc;
    private final Environment environment;

    public ConfiguracionAdministrador(JdbcTemplate jdbc, Environment environment) {
        this.jdbc = jdbc;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer admins = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE UPPER(rol) = 'ADMINISTRADOR'", Integer.class);
        if (admins != null && admins > 0) {
            return;
        }

        String correo = required("ADMIN_CORREO");
        String clave = required("ADMIN_CLAVE");
        String documento = required("ADMIN_DOCUMENTO");
        String nombres = required("ADMIN_NOMBRES");
        String apellidos = required("ADMIN_APELLIDOS");
        if (correo == null || clave == null || documento == null || nombres == null || apellidos == null) {
            logger.warn("No hay administrador configurado. Configure ADMIN_CORREO, ADMIN_CLAVE, ADMIN_DOCUMENTO, ADMIN_NOMBRES y ADMIN_APELLIDOS, y reinicie la aplicación.");
            return;
        }

        Integer correoUsado = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE correo = ?", Integer.class, correo);
        Integer documentoUsado = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE documento = ?", Integer.class, documento);
        if ((correoUsado != null && correoUsado > 0) || (documentoUsado != null && documentoUsado > 0)) {
            throw new IllegalStateException("No se pudo crear el administrador inicial: el correo o documento ya está registrado.");
        }

        jdbc.update("INSERT INTO usuario (nombres, apellidos, documento, correo, contrasena, rol, estado) "
                        + "VALUES (?, ?, ?, ?, ?, 'ADMINISTRADOR', 'ACTIVO')",
                nombres, apellidos, documento, correo, clave);
        logger.info("Se creó el primer administrador usando las variables ADMIN_* configuradas.");
    }

    private String required(String key) {
        String value = environment.getProperty(key);
        return value == null || value.isBlank() ? null : value.trim();
    }
}






