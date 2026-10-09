package com.example.demo.cancha;

import java.util.List;

public interface CanchaDAO {

    List<Cancha> listarTodas();

    List<Cancha> listarDisponibles();

    Cancha obtenerPorId(Long idCancha);

    int guardar(Cancha cancha);

    int actualizar(Cancha cancha);

    int cambiarEstado(Long idCancha, String nuevoEstado);

    long contarPorCategoria(Long idCategoria);
}