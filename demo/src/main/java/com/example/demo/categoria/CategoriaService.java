package com.example.demo.categoria;

import java.util.List;

public interface CategoriaService {
    List<Categoria> listarTodas();
    List<Categoria> listarActivas();
    Categoria obtenerPorId(Long id);
    void guardar(Categoria categoria);
    void actualizar(Categoria categoria);
    void cambiarEstado(Long id, String estado);
    boolean eliminar(Long id);
}