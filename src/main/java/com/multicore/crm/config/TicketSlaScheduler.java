package com.multicore.crm.config;

import com.multicore.crm.entity.Ticket;
import com.multicore.crm.entity.User;
import com.multicore.crm.repository.TicketHistoryRepository;
import com.multicore.crm.repository.TicketRepository;
import com.multicore.crm.service.NotificationService;
import com.multicore.crm.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketSlaScheduler {

    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final NotificationService notificationService;
    private final TicketService ticketService;

    @Scheduled(fixedDelayString = "${tickets.sla.check.ms:300000}")
    public void checkBreaches() {
        List<Ticket> atRisk = ticketRepository.findBySlaDueAtBeforeAndStatusIn(
                LocalDateTime.now(),
                List.of(Ticket.Status.OPEN, Ticket.Status.IN_PROGRESS)
        );

        for (Ticket ticket : atRisk) {
            try {
                if (Boolean.TRUE.equals(ticket.getIsEscalated())) {
                    continue; // already escalated once
                }
                ticket.setIsEscalated(true);
                ticket.setPriority(Ticket.Priority.URGENT);
                ticket.setSlaDueAt(ticketService.calculateSlaDue(Ticket.Priority.URGENT));
                ticketRepository.save(ticket);

                recordHistory(ticket, null, "SLA_BREACH", null, "auto-escalated");

                User target = ticket.getAssignedTo() != null ? ticket.getAssignedTo() : ticket.getCreatedBy();
                notificationService.notifyUser(
                        target,
                        "Ticket SLA breached",
                        "Ticket #" + ticket.getId() + " has breached SLA and was auto-escalated."
                );
            } catch (Exception e) {
                log.error("SLA breach handling failed for ticket {}: {}", ticket.getId(), e.getMessage());
            }
        }
    }

    private void recordHistory(Ticket ticket, User actor, String action, String oldValue, String newValue) {
        var history = new com.multicore.crm.entity.TicketHistory();
        history.setTicket(ticket);
        history.setUser(actor);
        history.setAction(action);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        ticketHistoryRepository.save(history);
    }
}

