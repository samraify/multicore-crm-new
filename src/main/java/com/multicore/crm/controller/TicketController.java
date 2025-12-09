package com.multicore.crm.controller;

import com.multicore.crm.dto.ticket.AddCommentDTO;
import com.multicore.crm.dto.ticket.CreateTicketDTO;
import com.multicore.crm.dto.ticket.TicketAnalyticsDTO;
import com.multicore.crm.dto.ticket.UpdateTicketStatusDTO;
import com.multicore.crm.entity.Ticket;
import com.multicore.crm.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BUSINESS_ADMIN','SUPPORT_MANAGER','SUPPORT_AGENT','VIEWER','CUSTOMER')")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> create(@Valid @RequestBody CreateTicketDTO dto, HttpServletRequest req) {
        Long businessId = (Long) req.getAttribute("businessId");
        Long userId = getUserId(req);
        Ticket ticket = ticketService.createTicket(dto, businessId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping("/business/{businessId}")
    public Page<Ticket> list(@PathVariable Long businessId,
                             @RequestParam(required = false) Ticket.Status status,
                             @RequestParam(required = false) Ticket.Priority priority,
                             @RequestParam(required = false) Long assignedTo,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        return ticketService.listByBusiness(businessId, requesterBusinessId, status, priority, assignedTo, page, size);
    }

    @GetMapping("/{ticketId}")
    public Ticket get(@PathVariable Long ticketId, HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        return ticketService.getTicket(ticketId, requesterBusinessId);
    }

    @PatchMapping("/{ticketId}/status")
    public Ticket updateStatus(@PathVariable Long ticketId,
                               @Valid @RequestBody UpdateTicketStatusDTO dto,
                               HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        Long userId = getUserId(req);
        return ticketService.updateStatus(ticketId, dto, requesterBusinessId, userId);
    }

    @PatchMapping("/{ticketId}/assign")
    public Ticket assign(@PathVariable Long ticketId,
                         @RequestParam Long assigneeId,
                         HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        Long userId = getUserId(req);
        return ticketService.assignTicket(ticketId, assigneeId, requesterBusinessId, userId);
    }

    @PostMapping("/{ticketId}/comments")
    public Ticket addComment(@PathVariable Long ticketId,
                             @Valid @RequestBody AddCommentDTO dto,
                             HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        Long userId = getUserId(req);
        return ticketService.addComment(ticketId, dto, requesterBusinessId, userId);
    }

    @PostMapping("/{ticketId}/escalate")
    public Ticket escalate(@PathVariable Long ticketId, HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        Long userId = getUserId(req);
        return ticketService.escalate(ticketId, requesterBusinessId, userId);
    }

    @GetMapping("/analytics/{businessId}")
    public TicketAnalyticsDTO analytics(@PathVariable Long businessId, HttpServletRequest req) {
        Long requesterBusinessId = (Long) req.getAttribute("businessId");
        return ticketService.analytics(businessId, requesterBusinessId);
    }

    private Long getUserId(HttpServletRequest req) {
        Object userIdObj = req.getAttribute("userId");
        if (userIdObj instanceof Long) return (Long) userIdObj;
        return null;
    }
}

