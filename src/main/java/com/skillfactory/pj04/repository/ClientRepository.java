package com.skillfactory.pj04.repository;

import com.skillfactory.pj04.client.Client;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Client q WHERE q.id = :id")
    Optional<Client> findByIdForUpdate(@Param("id") int id);
}
