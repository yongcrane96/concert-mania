package com.example.repository;

import com.example.domain.concert.Concert;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @EntityGraph(attributePaths = {"seats"})
    Optional<Concert> findWithSeatsById(Long id);
}