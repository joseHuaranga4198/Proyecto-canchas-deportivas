package com.example.demo.categoria;

import java.util.List;
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
        return categoriaDAO.obtenerPorId(idCategoria);
    }

    @Override
    public void guardar(Categoria categoria) {
        if (categoria.getEstado() == null || categoria.getEstado().isEmpty()) {
            categoria.setEstado("ACTIVA");
        }
        categoriaDAO.guardar(categoria);
    }

    @Override
    public void actualizar(Categoria categoria) {
        categoriaDAO.actualizar(categoria);
    }

    @Override
    public boolean eliminar(Long idCategoria) {
        // Regla: No eliminar si tiene canchas vinculadas
        if (categoriaDAO.contarCanchasAsociadas(idCategoria) > 0) {
            return false;
        }
        categoriaDAO.eliminar(idCategoria);
        return true;
    }
}