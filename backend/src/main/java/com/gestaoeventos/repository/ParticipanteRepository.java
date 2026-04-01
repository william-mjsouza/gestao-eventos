package com.gestaoeventos.repository;

import com.gestaoeventos.entity.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante,String> {
    boolean existsByEmail(String email);
    Optional<Participante> findByEmail(String email);

}
