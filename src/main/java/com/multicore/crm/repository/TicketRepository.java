package com.multicore.crm.repository;

import com.multicore.crm.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByBusinessId(Long businessId);
    long countByBusinessIdAndStatus(Long businessId, Ticket.Status status);
    long countByBusinessIdAndPriority(Long businessId, Ticket.Priority priority);
    List<Ticket> findBySlaDueAtBeforeAndStatusIn(LocalDateTime dateTime, List<Ticket.Status> statuses);
}

