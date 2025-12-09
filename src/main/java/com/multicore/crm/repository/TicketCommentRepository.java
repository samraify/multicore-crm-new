package com.multicore.crm.repository;

import com.multicore.crm.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
}

