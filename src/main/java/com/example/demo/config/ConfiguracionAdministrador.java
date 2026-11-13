package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Cuenta fija de la demostración académica. No reemplaza administradores existentes.
 */
@Component
public class ConfiguracionAdministrador implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionAdministrador.class);
    public static final String CORREO = "admin@canchas.pe";
    public static final String CLAVE = "admin123";

    private final JdbcTemplate jdbc;

    public ConfiguracionAdministrador(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer admins = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE UPPER(rol) = 'ADMINISTRADOR'", Integer.class);
        if (admins != null && admins > 0) {
            migrarClaves();
            return;
        }

        String correo = CORREO;
        String clave = CLAVE;
        String documento = "00000001";
        String nombres = "Administrador";
        String apellidos = "CanchaYa";

        Integer correoUsado = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE LOWER(correo) = LOWER(?)", Integer.class, correo);
        Integer documentoUsado = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE documento = ?", Integer.class, documento);
        if ((correoUsado != null && correoUsado > 0) || (documentoUsado != null && documentoUsado > 0)) {
            throw new IllegalStateException("No se pudo crear el administrador inicial: el correo o documento ya está registrado.");
        }

        jdbc.update("INSERT INTO usuario (nombres, apellidos, documento, correo, contrasena, rol, estado) "
                        + "VALUES (?, ?, ?, ?, ?, 'ADMINISTRADOR', 'ACTIVO')",
                nombres, apellidos, documento, correo, Claves.hash(clave));
        migrarClaves();
        logger.info("Se creó el administrador de demostración: {}", CORREO);
    }

    private void migrarClaves() {
        jdbc.query("SELECT id_usuario, contrasena FROM usuario", (org.springframework.jdbc.core.RowCallbackHandler) rs -> {
            String clave = rs.getString("contrasena");
            if (!Claves.esHash(clave)) jdbc.update("UPDATE usuario SET contrasena=? WHERE id_usuario=?", Claves.hash(clave), rs.getLong("id_usuario"));
        });
    }

}






