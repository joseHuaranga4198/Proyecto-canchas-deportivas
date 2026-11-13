package com.example.demo.validacion;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

/** Reglas de entrada compartidas por los servicios, independientes de los formularios. */
public final class Validaciones {
    private Validaciones() { }

    public static Long id(Long valor, String campo) {
        if (valor == null || valor <= 0) throw new IllegalArgumentException(campo + " debe ser un identificador válido.");
        return valor;
    }

    public static String texto(String valor, String campo, int maximo, boolean obligatorio) {
        String texto = valor == null ? "" : valor.trim();
        if (obligatorio && texto.isEmpty()) throw new IllegalArgumentException(campo + " es obligatorio.");
        if (texto.length() > maximo) throw new IllegalArgumentException(campo + " admite hasta " + maximo + " caracteres.");
        return texto;
    }

    public static String estado(String valor, String predeterminado, String... permitidos) {
        String estado = valor == null || valor.isBlank() ? predeterminado : valor.trim().toUpperCase(Locale.ROOT);
        if (estado == null || !Set.of(permitidos).contains(estado)) throw new IllegalArgumentException("El estado no es válido.");
        return estado;
    }

    public static BigDecimal precio(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0 || valor.compareTo(new BigDecimal("99999999.99")) > 0
                || valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("El precio debe ser positivo, con hasta dos decimales y ocho dígitos enteros.");
        }
        return valor;
    }
}
