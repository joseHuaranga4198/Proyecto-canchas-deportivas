package com.example.demo.categoria;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final List<Categoria> categorias = new ArrayList<>();

    public CategoriaServiceImpl() {
        categorias.add(new Categoria(1L, "Fútbol 5", "Grass sintético para 10 personas", "ACTIVA"));
        categorias.add(new Categoria(2L, "Fútbol 7", "Grass sintético reglamentario", "ACTIVA"));
        categorias.add(new Categoria(3L, "Pádel", "Canchas panorámicas de cristal templado", "ACTIVA"));
        categorias.add(new Categoria(4L, "Básquet", "Losa deportiva pulida", "ACTIVA"));
    }

    @Override
    public List<Categoria> listarTodas() {
        return categorias;
    }

    @Override
    public List<Categoria> listarActivas() {
        return categorias.stream()
                .filter(c -> "ACTIVA".equalsIgnoreCase(c.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public Categoria obtenerPorId(Long id) {
        return categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void guardar(Categoria categoria) {
        categoria.setId((long) (categorias.size() + 1));
        if (categoria.getEstado() == null) categoria.setEstado("ACTIVA");
        categorias.add(categoria);
    }

    @Override
    public void actualizar(Categoria categoria) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId().equals(categoria.getId())) {
                categorias.set(i, categoria);
                break;
            }
        }
    }

    @Override
    public void cambiarEstado(Long id, String estado) {
        Categoria c = obtenerPorId(id);
        if (c != null) {
            c.setEstado(estado);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        return categorias.removeIf(c -> c.getId().equals(id));
    }
}