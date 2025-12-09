package com.multicore.crm.service;

import com.multicore.crm.dto.ticket.AddCommentDTO;
import com.multicore.crm.dto.ticket.CreateTicketDTO;
import com.multicore.crm.dto.ticket.TicketAnalyticsDTO;
import com.multicore.crm.dto.ticket.UpdateTicketStatusDTO;
import com.multicore.crm.entity.*;
import com.multicore.crm.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final SLARepository slaRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    public Ticket createTicket(@Valid CreateTicketDTO dto, Long requesterBusinessId, Long requesterUserId) {
        enforceSameBusiness(dto.getBusinessId(), requesterBusinessId);
        Business business = businessRepository.findById(dto.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));
        User createdBy = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setBusiness(business);
        ticket.setCreatedBy(createdBy);
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setIsEscalated(false);

        if (dto.getAssignedToUserId() != null) {
            User assignee = userRepository.findById(dto.getAssignedToUserId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            ticket.setAssignedTo(assignee);
        }

        // SLA due date based on priority
        ticket.setSlaDueAt(calculateSlaDue(dto.getPriority()));

        Ticket saved = ticketRepository.save(ticket);
        recordHistory(saved, createdBy, "CREATE", null, "Ticket created");
        return saved;
    }

    public Ticket updateStatus(Long ticketId, UpdateTicketStatusDTO dto, Long requesterBusinessId, Long userId) {
        Ticket ticket = getTicketForBusiness(ticketId, requesterBusinessId);
        Ticket.Status old = ticket.getStatus();
        ticket.setStatus(dto.getStatus());
        ticketRepository.save(ticket);
        User actor = userRepository.findById(userId).orElse(null);
        recordHistory(ticket, actor, "STATUS_CHANGE", old.name(), dto.getStatus().name());
        return ticket;
    }

    public Ticket assignTicket(Long ticketId, Long assigneeId, Long requesterBusinessId, Long userId) {
        Ticket ticket = getTicketForBusiness(ticketId, requesterBusinessId);
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
        User actor = userRepository.findById(userId).orElse(null);
        Long oldAssignee = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null;
        ticket.setAssignedTo(assignee);
        ticketRepository.save(ticket);
        recordHistory(ticket, actor, "ASSIGN", oldAssignee == null ? "none" : oldAssignee.toString(), assigneeId.toString());
        return ticket;
    }

    public Ticket escalate(Long ticketId, Long requesterBusinessId, Long userId) {
        Ticket ticket = getTicketForBusiness(ticketId, requesterBusinessId);
        ticket.setIsEscalated(true);
        ticket.setPriority(Ticket.Priority.URGENT);
        ticket.setSlaDueAt(calculateSlaDue(Ticket.Priority.URGENT));
        ticketRepository.save(ticket);
        User actor = userRepository.findById(userId).orElse(null);
        recordHistory(ticket, actor, "ESCALATE", null, "URGENT");
        return ticket;
    }

    public Ticket addComment(Long ticketId, AddCommentDTO dto, Long requesterBusinessId, Long userId) {
        Ticket ticket = getTicketForBusiness(ticketId, requesterBusinessId);
        User actor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setUser(actor);
        comment.setComment(dto.getComment());
        ticketCommentRepository.save(comment);

        recordHistory(ticket, actor, "COMMENT", null, "comment added");
        return ticket;
    }

    public Page<Ticket> listByBusiness(Long businessId,
                                       Long requesterBusinessId,
                                       Ticket.Status status,
                                       Ticket.Priority priority,
                                       Long assignedTo,
                                       int page,
                                       int size) {
        enforceSameBusiness(businessId, requesterBusinessId);
        Specification<Ticket> spec = businessEquals(businessId);
        if (status != null) spec = spec.and(statusEquals(status));
        if (priority != null) spec = spec.and(priorityEquals(priority));
        if (assignedTo != null) spec = spec.and(assignedToEquals(assignedTo));
        return ticketRepository.findAll(spec, PageRequest.of(page, size));
    }

    public Ticket getTicket(Long ticketId, Long requesterBusinessId) {
        return getTicketForBusiness(ticketId, requesterBusinessId);
    }

    public TicketAnalyticsDTO analytics(Long businessId, Long requesterBusinessId) {
        enforceSameBusiness(businessId, requesterBusinessId);
        long total = ticketRepository.findByBusinessId(businessId).size();
        return TicketAnalyticsDTO.builder()
                .total(total)
                .open(ticketRepository.countByBusinessIdAndStatus(businessId, Ticket.Status.OPEN))
                .inProgress(ticketRepository.countByBusinessIdAndStatus(businessId, Ticket.Status.IN_PROGRESS))
                .resolved(ticketRepository.countByBusinessIdAndStatus(businessId, Ticket.Status.RESOLVED))
                .closed(ticketRepository.countByBusinessIdAndStatus(businessId, Ticket.Status.CLOSED))
                .low(ticketRepository.countByBusinessIdAndPriority(businessId, Ticket.Priority.LOW))
                .medium(ticketRepository.countByBusinessIdAndPriority(businessId, Ticket.Priority.MEDIUM))
                .high(ticketRepository.countByBusinessIdAndPriority(businessId, Ticket.Priority.HIGH))
                .urgent(ticketRepository.countByBusinessIdAndPriority(businessId, Ticket.Priority.URGENT))
                .build();
    }

    private void enforceSameBusiness(Long targetBusinessId, Long requesterBusinessId) {
        if (targetBusinessId == null || requesterBusinessId == null || !targetBusinessId.equals(requesterBusinessId)) {
            throw new RuntimeException("Cross-tenant access denied");
        }
    }

    private Ticket getTicketForBusiness(Long ticketId, Long requesterBusinessId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        enforceSameBusiness(ticket.getBusiness().getId(), requesterBusinessId);
        return ticket;
    }

    private void recordHistory(Ticket ticket, User actor, String action, String oldValue, String newValue) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setUser(actor);
        history.setAction(action);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        ticketHistoryRepository.save(history);
    }

    public LocalDateTime calculateSlaDue(Ticket.Priority priority) {
        SLA.PriorityLevel level = switch (priority) {
            case LOW -> SLA.PriorityLevel.LOW;
            case MEDIUM -> SLA.PriorityLevel.MEDIUM;
            case HIGH -> SLA.PriorityLevel.HIGH;
            case URGENT -> SLA.PriorityLevel.URGENT;
        };

        Integer hours = slaRepository.findByPriorityLevel(level)
                .map(SLA::getAllowedHours)
                .orElse(24); // default
        return LocalDateTime.now().plusHours(hours);
    }

    // ---- Specifications ----
    private Specification<Ticket> businessEquals(Long businessId) {
        return (root, query, cb) -> cb.equal(root.get("business").get("id"), businessId);
    }

    private Specification<Ticket> statusEquals(Ticket.Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private Specification<Ticket> priorityEquals(Ticket.Priority priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    private Specification<Ticket> assignedToEquals(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), userId);
    }
}

