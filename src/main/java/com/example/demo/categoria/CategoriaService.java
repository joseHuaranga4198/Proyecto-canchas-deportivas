package com.example.demo.categoria;

import java.util.List;

public interface CategoriaService {

    List<Categoria> listarTodas();

    List<Categoria> listarActivas();

    Categoria obtenerPorId(Long idCategoria);

    void guardar(Categoria categoria);

    void actualizar(Categoria categoria);

    boolean eliminar(Long idCategoria);
}