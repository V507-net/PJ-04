package com.skillfactory.pj04.repository;

import com.skillfactory.pj04.banking.Operation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    @Query(value = "SELECT * FROM operations o " +
                   "WHERE (:clientId IS NULL OR o.from_id = :clientId OR o.to_id = :clientId) " +
                   "AND (o.operation_datetime >= COALESCE(:startDate, '1000-01-01'::timestamp)) " +
                   "AND (o.operation_datetime <= COALESCE(:endDate, '3000-01-01'::timestamp))",
            nativeQuery = true)
    List<Operation> findByFilters(
            @Param("clientId") Integer clientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
