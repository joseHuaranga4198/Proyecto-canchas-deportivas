package com.example.demo.config;

import java.util.Locale;
import com.example.demo.seguridad.AccesoAdminInterceptor;
import com.example.demo.seguridad.AccesoSesionInterceptor;
import com.example.demo.seguridad.ProteccionCsrf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final com.example.demo.usuario.UsuarioService usuarios;

    public WebConfig(com.example.demo.usuario.UsuarioService usuarios) { this.usuarios = usuarios; }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper rutas = new UrlPathHelper();
        rutas.setRemoveSemicolonContent(true);
        configurer.setPatternParser(null);
        configurer.setUrlPathHelper(rutas);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("es"));
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccesoSesionInterceptor(usuarios))
                .addPathPatterns("/**").excludePathPatterns("/css/**", "/images/**", "/js/**", "/error");
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(new AccesoAdminInterceptor())
                .addPathPatterns("/admin/**", "/reservas/admin", "/reservas/admin/**", "/canchas/admin/**", "/usuario/admin/**");
        registry.addInterceptor(new ProteccionCsrf()).addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/images/**", "/js/**", "/error");
    }
    
}
