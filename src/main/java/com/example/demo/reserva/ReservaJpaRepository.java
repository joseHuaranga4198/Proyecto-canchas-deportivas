package com.example.demo.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaJpaRepository extends JpaRepository<Reserva, Long> {
    @EntityGraph(attributePaths = {"usuario", "cancha", "cancha.categoria", "pago"})
    List<Reserva> findAllByUsuario_IdUsuarioOrderByFechaDescHoraInicioDesc(Long idUsuario);

    @EntityGraph(attributePaths = {"usuario", "cancha", "cancha.categoria", "pago"})
    @Query("select r from Reserva r where r.idReserva = :id")
    Optional<Reserva> findDetailById(@Param("id") Long id);

    @Query("select count(r) > 0 from Reserva r where r.cancha.idCancha = :canchaId and r.fecha = :fecha "
            + "and r.estado <> 'CANCELADA' and :inicio < r.horaFin and :fin > r.horaInicio")
    boolean existsOverlapping(@Param("canchaId") Long canchaId, @Param("fecha") LocalDate fecha,
                              @Param("inicio") LocalTime inicio, @Param("fin") LocalTime fin);

    @EntityGraph(attributePaths = {"usuario", "cancha", "cancha.categoria", "pago"})
    @Query("select r from Reserva r where (:id is null or r.idReserva = :id) "
            + "and (:cliente is null or lower(concat(concat(r.usuario.nombres, ' '), r.usuario.apellidos)) like lower(concat('%', :cliente, '%')) "
            + "or lower(r.usuario.correo) like lower(concat('%', :cliente, '%')) or lower(r.usuario.documento) like lower(concat('%', :cliente, '%'))) "
            + "and (:cancha is null or lower(r.cancha.nombre) like lower(concat('%', :cancha, '%'))) "
            + "and (:estado is null or upper(r.estado) = upper(:estado)) "
            + "and (:desde is null or r.fecha >= :desde) and (:hasta is null or r.fecha <= :hasta) "
            + "order by r.fecha desc, r.horaInicio desc")
    List<Reserva> buscarAdministrativamente(@Param("id") Long id, @Param("cliente") String cliente,
            @Param("cancha") String cancha, @Param("estado") String estado,
            @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @EntityGraph(attributePaths = {"usuario", "cancha", "cancha.categoria", "pago"})
    List<Reserva> findByUsuario_IdUsuarioAndEstadoIgnoreCaseOrderByFechaDescHoraInicioDesc(Long idUsuario, String estado);
}
