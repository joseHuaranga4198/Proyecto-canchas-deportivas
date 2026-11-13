package com.example.demo.cancha;

import java.util.List;
import com.example.demo.validacion.Validaciones;
import com.example.demo.categoria.Categoria;
import com.example.demo.categoria.CategoriaDAO;
import org.springframework.stereotype.Service;

@Service
public class CanchaServiceImpl implements CanchaService {

    private final CanchaDAO canchaDAO;
    private final CategoriaDAO categorias;

    public CanchaServiceImpl(CanchaDAO canchaDAO, CategoriaDAO categorias) {
        this.canchaDAO = canchaDAO;
        this.categorias = categorias;
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
        Validaciones.id(idCancha, "La cancha");
        return canchaDAO.obtenerPorId(idCancha);
    }

    @Override
    public void guardar(Cancha cancha) {
        validar(cancha);
        cancha.setIdCancha(null);
        canchaDAO.guardar(cancha);
    }

    @Override
    public void actualizar(Cancha cancha) {
        if (cancha == null || obtenerPorId(cancha.getIdCancha()) == null) {
            throw new IllegalArgumentException("No se encontró la cancha.");
        }
        validar(cancha);
        canchaDAO.actualizar(cancha);
    }

    @Override
    public void cambiarEstado(Long idCancha, String nuevoEstado) {
        Cancha cancha = obtenerPorId(idCancha);
        if (cancha == null) throw new IllegalArgumentException("No se encontró la cancha.");
        nuevoEstado = Validaciones.estado(nuevoEstado, null, "ACTIVA", "INACTIVA");
        cancha.setEstado(nuevoEstado);
        validar(cancha);
        canchaDAO.cambiarEstado(idCancha, nuevoEstado);
    }

    private void validar(Cancha cancha) {
        if (cancha == null) throw new IllegalArgumentException("La cancha es obligatoria.");
        cancha.setNombre(Validaciones.texto(cancha.getNombre(), "Nombre", 100, true));
        cancha.setCaracteristicas(Validaciones.texto(cancha.getCaracteristicas(), "Características", 500, false));
        cancha.setPrecioHora(Validaciones.precio(cancha.getPrecioHora()));
        cancha.setEstado(Validaciones.estado(cancha.getEstado(), "ACTIVA", "ACTIVA", "INACTIVA"));
        if (cancha.getCategoria() == null) throw new IllegalArgumentException("Selecciona una categoría válida.");
        Long id = Validaciones.id(cancha.getCategoria().getIdCategoria(), "La categoría");
        Categoria categoria = categorias.obtenerPorId(id);
        if (categoria == null || ("ACTIVA".equals(cancha.getEstado()) && !"ACTIVA".equalsIgnoreCase(categoria.getEstado()))) {
            throw new IllegalArgumentException("Una cancha activa debe pertenecer a una categoría activa existente.");
        }
        cancha.setCategoria(categoria);
    }
}
