package com.example.demo.config;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public final class Claves {
    private static final Pbkdf2PasswordEncoder ENCODER = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private Claves() { }
    public static boolean esHash(String valor) { return valor != null && valor.startsWith("{pbkdf2}"); }
    public static String hash(String valor) { return "{pbkdf2}" + ENCODER.encode(valor); }
    public static boolean coincide(String valor, String hash) { return esHash(hash) && ENCODER.matches(valor, hash.substring(8)); }
}
