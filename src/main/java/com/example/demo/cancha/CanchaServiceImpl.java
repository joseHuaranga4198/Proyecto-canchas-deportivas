package com.example.demo.cancha;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CanchaServiceImpl implements CanchaService {

    private final CanchaDAO canchaDAO;

    public CanchaServiceImpl(CanchaDAO canchaDAO) {
        this.canchaDAO = canchaDAO;
    }

    @Override
    public List<Cancha> listarTodas() {
        return canchaDAO.listarTodas();
    }

    @Override
    public List<Cancha> listarDisponibles() {
        return canchaDAO.listarDisponibles();
    }

    @Override
    public Cancha obtenerPorId(Long idCancha) {
        return canchaDAO.obtenerPorId(idCancha);
    }

    @Override
    public void guardar(Cancha cancha) {
        if (cancha.getEstado() == null || cancha.getEstado().isEmpty()) {
            cancha.setEstado("ACTIVA");
        }
        canchaDAO.guardar(cancha);
    }

    @Override
    public void actualizar(Cancha cancha) {
        canchaDAO.actualizar(cancha);
    }

    @Override
    public void cambiarEstado(Long idCancha, String nuevoEstado) {
        canchaDAO.cambiarEstado(idCancha, nuevoEstado);
    }
}