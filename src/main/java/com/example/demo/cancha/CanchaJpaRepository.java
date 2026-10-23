package com.example.demo.cancha;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CanchaJpaRepository extends JpaRepository<Cancha, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cancha c where c.idCancha = :id")
    Optional<Cancha> findForReservation(@Param("id") Long id);
}
