package com.example.demo.cancha;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanchaServiceImpl implements CanchaService {

    private final List<Cancha> canchas = new ArrayList<>();

    public CanchaServiceImpl() {
        // Datos iniciales de prueba para visualizar las interfaces
        canchas.add(new Cancha(1L, "Cancha Monumental 1", "Fútbol 5", "Grass sintético Pro, luz LED", new BigDecimal("60.00"), "DISPONIBLE"));
        canchas.add(new Cancha(2L, "Cancha Monumental 2", "Fútbol 5", "Grass sintético, techada", new BigDecimal("50.00"), "DISPONIBLE"));
        canchas.add(new Cancha(3L, "Cancha Los Andes 7", "Fútbol 7", "Grass sintético reglamentario, camerinos", new BigDecimal("90.00"), "DISPONIBLE"));
        canchas.add(new Cancha(4L, "Pádel Center 1", "Pádel", "Césped azul texturado, vidrio panorámico", new BigDecimal("45.00"), "MANTENIMIENTO"));
    }

    @Override
    public List<Cancha> listarTodas() {
        return canchas;
    }

    @Override
    public List<Cancha> listarDisponibles() {
        return canchas.stream()
                .filter(c -> "DISPONIBLE".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public Cancha obtenerPorId(Long id) {
        return canchas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void guardar(Cancha cancha) {
        cancha.setId((long) (canchas.size() + 1));
        canchas.add(cancha);
    }

    @Override
    public void actualizar(Cancha cancha) {
        for (int i = 0; i < canchas.size(); i++) {
            if (canchas.get(i).getId().equals(cancha.getId())) {
                canchas.set(i, cancha);
                break;
            }
        }
    }

    @Override
    public void cambiarEstado(Long id, String nuevoEstado) {
        Cancha c = obtenerPorId(id);
        if (c != null) {
            c.setEstado(nuevoEstado);
        }
    }
}