package com.example.demo.cancha;

import java.util.List;

public interface CanchaService {

    List<Cancha> listarTodas();

    List<Cancha> listarDisponibles();

    Cancha obtenerPorId(Long idCancha);

    void guardar(Cancha cancha);

    void actualizar(Cancha cancha);

    void cambiarEstado(Long idCancha, String nuevoEstado);
}