package com.example.demo.categoria;

import java.util.List;

public interface CategoriaDAO {

    List<Categoria> listarTodas();

    List<Categoria> listarActivas();

    Categoria obtenerPorId(Long idCategoria);

    int guardar(Categoria categoria);

    int actualizar(Categoria categoria);

    int cambiarEstado(Long idCategoria, String estado);
}