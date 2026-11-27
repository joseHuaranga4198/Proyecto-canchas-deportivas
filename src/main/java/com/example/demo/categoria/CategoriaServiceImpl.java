package com.example.demo.categoria;

import java.util.List;
import com.example.demo.validacion.Validaciones;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaServiceImpl(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public List<Categoria> listarTodas() {
        return categoriaDAO.listarTodas();
    }

    @Override
    public List<Categoria> listarActivas() {
        return categoriaDAO.listarActivas();
    }

    @Override
    public Categoria obtenerPorId(Long idCategoria) {
        Validaciones.id(idCategoria, "La categoría");
        return categoriaDAO.obtenerPorId(idCategoria);
    }

    @Override
    public void guardar(Categoria categoria) {
        validar(categoria);
        categoria.setIdCategoria(null);
        try { categoriaDAO.guardar(categoria); }
        catch (DuplicateKeyException ex) { throw new IllegalArgumentException("Ya existe una categoría con ese nombre."); }
    }

    @Override
    public void actualizar(Categoria categoria) {
        if (categoria == null || obtenerPorId(categoria.getIdCategoria()) == null) {
            throw new IllegalArgumentException("No se encontró la categoría.");
        }
        validar(categoria);
        try { categoriaDAO.actualizar(categoria); }
        catch (DuplicateKeyException ex) { throw new IllegalArgumentException("Ya existe una categoría con ese nombre."); }
    }

    @Override
    public void cambiarEstado(Long idCategoria, String estado) {
        if (obtenerPorId(idCategoria) == null) throw new IllegalArgumentException("No se encontró la categoría.");
        estado = Validaciones.estado(estado, null, "ACTIVA", "INACTIVA");
        categoriaDAO.cambiarEstado(idCategoria, estado);
    }

    private void validar(Categoria categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoría es obligatoria.");
        categoria.setNombre(Validaciones.texto(categoria.getNombre(), "Nombre", 50, true));
        categoria.setDescripcion(Validaciones.texto(categoria.getDescripcion(), "Descripción", 255, false));
        categoria.setEstado(Validaciones.estado(categoria.getEstado(), "ACTIVA", "ACTIVA", "INACTIVA"));
        boolean duplicada = categoriaDAO.listarTodas().stream().anyMatch(c ->
                c.getNombre().equalsIgnoreCase(categoria.getNombre()) && !c.getIdCategoria().equals(categoria.getIdCategoria()));
        if (duplicada) throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
    }
}
