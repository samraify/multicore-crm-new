# 📘 MultiCore CRM — Full Project Specification (Team Documentation)

*A Multi-Tenant, Role-Based, AI-assisted CRM backend built with Spring Boot & PostgreSQL.*

---

## 1. Project Overview

MultiCore CRM is a multi-tenant, role-based, AI-assisted CRM backend system that lets multiple companies manage leads, customers, deals, tasks, support tickets, and interactions, with strict business-level isolation.

APIs cover authentication, business onboarding, lead lifecycle, customers, deals/pipelines, support tickets, appointments/tasks, audit logs/analytics, and AI-assisted lead/customer matching. Stack: Spring Boot (Web, Data JPA, Security, Validation), PostgreSQL, Hibernate, JWT, RBAC, soft delete + audit logs, transactional workflows, tenant-scoped requests.

---

## 2. System Architecture Overview

| Layer     | Technology                                        |
| --------- | ------------------------------------------------- |
| Language  | Java 21                                           |
| Framework | Spring Boot (Web, Data JPA, Security, Validation) |
| Database  | PostgreSQL                                        |
| ORM       | Hibernate                                         |
| Security  | JWT, BCrypt, RBAC                                 |
| Logging   | Spring AOP, Audit Logging                         |
| AI        | Lead Matching Engine, Recommendation Engine       |
| Other     | Lombok, ModelMapper                               |

---

## 3. Multi-Tenancy Model

* Discriminator field `business_id` isolates domain data.
* Each business has its own users, leads, customers, deals, tasks, products, tickets.
* Super Admin manages the platform but cannot access company data.
* Strict isolation: users from one business cannot see another business’s data.

---

## 4. User Roles & Access Levels

| Role                       | Access Level                                                  |
| -------------------------- | ------------------------------------------------------------- |
| Super Admin                | Full platform control (tenants, platform analytics, settings) |
| Business Admin (Owner)     | Full control within a single business                         |
| Sales Manager              | Manage sales team, leads, deals                               |
| Sales Agent                | Assigned leads, tasks, interactions                           |
| Support Manager            | Manage tickets, SLAs                                          |
| Support Agent              | Handle customer issues                                        |
| Finance                    | Billing, invoices, reports                                    |
| Viewer / Read-Only         | Can only view (no editing)                                    |
| Customer                   | Front-facing portal access                                    |

---

## 5. Login / Registration Flow

* Super Admin: registered manually in DB; login only; can activate/deactivate businesses and monitor the platform.
* Owner (Business Admin): registered by Super Admin; login only; manages staff, business settings, pipelines, support categories.
* Staff (Sales + Support): created by Owner; login only; scoped to business; permissions depend on role.
* Customer: self-register + login; customer portal access for tickets, appointments, documents (optional), profile.

---

## 6. Core Features — Full Breakdown

### A. Platform-Level (Super Admin)
* Multi-tenant onboarding and isolation.
* Tenant management (activate/deactivate, subscription plans, usage stats).
* Global system settings (modules, rules, announcements).
* Platform monitoring (tenants, users, global entities, system health, AI performance).

### B. Company-Level (Owners + Staff)
1) User & Role Management: CRUD staff, assign RBAC roles/permissions, notifications, suspend/reactivate.  
2) Customer Management: profiles, segmentation, timeline, soft delete/restore, relationship types.  
3) Lead Management: statuses, scoring, filtering, notes/files, timeline, convert lead→customer transactionally, product/service linking.  
4) Follow-Up Tasks: assign, reminders, statuses, notifications, dashboards.  
5) Appointments: scheduling, assignees, status, calendar view, history.  
6) Products & Services Catalog: items with price/description/tags; link to leads/deals/tickets.  
7) Interaction Logging: calls/emails/meetings, notes, attachments, sentiment, timeline.  
8) Support Ticketing: customer-created tickets, categories/priorities, SLA tracking, history, attachments, internal notes, escalations, analytics.  
9) AI Features: lead matching engine (match score + explanation), recommendation engine with history.  
10) Analytics & Dashboards: leads, sales, tickets, growth, productivity, AI performance, appointments.  
11) Notifications: in-app/email (optional) for lead assignment, ticket updates, task reminders, appointment updates.  
12) Audit Logging: user, action, entity, entity ID, timestamp for every operation.  
13) Company Settings: business profile, branding, role permissions, pipeline stages, support categories/priorities.

### C. Customer Portal
* View profile.
* Submit/track tickets.
* View appointments.
* Access documents/invoices.
* Optional deal/service progress.

---

## 7. Entity Model (Descriptions)

1) User: email, password (BCrypt), name, phone, status, roles (M:M), belongs to 0–1 business via `UserBusiness`; authentication/authorization; multi-tenancy aware.  
2) Role: ADMIN, OWNER, SALES_AGENT, SALES_MANAGER, SUPPORT_AGENT, SUPPORT_MANAGER, FINANCE, VIEWER, CUSTOMER.  
3) Permission (optional): granular permissions like LEAD_VIEW/CREATE, TICKET_MANAGE.  
4) Business: tenant/company with name, address, industry, description, owner; links to customers, leads, deals, tickets, subscription.  
5) UserBusiness: maps users to businesses; stores role and join date (current design 1 user → 1 business).  
6) Customer: name, email, phone, address, notes, tags, segmentation, soft deletable, belongs to Business, interactions timeline.  
7) CustomerBusinessMatch: connects customers to multiple businesses with relationship types (prospect/client/vendor/partner).  
8) Lead: potential customer with contact info, status, score, assigned user, business id, notes/interactions.  
9) LeadActivity: timeline entries (calls/meetings/emails), notes, attachments.  
10) Deal: sales opportunity with amount, probability, stage, linked products, assigned seller.  
11) DealActivity: activities/history/attachments within a deal.  
12) FollowUpTask: reminders with assignee, due date, status, linked to customer/lead/deal.  
13) Ticket: support issue with category, priority, description, customer, status, assigned agent.  
14) TicketComment: comments by staff/customer, internal notes, attachments.  
15) TicketHistory: status transitions with timestamps and actor.  
16) SLA: response/resolution times and severity rules.  
17) Product: items a business sells (name, price, tags, category).  
18) BusinessService: services with name, description, price, active flag.  
19) SubscriptionPlan: plan name, features, monthly price.  
20) BusinessSubscription: associates business with plan, start/expiry, payment info.  
21) Notification: alerts with user id, title, message, read/unread, created at.  
22) AuditLog: user id, action, entity name, entity id, timestamp.  
23) Appointment: scheduling with customer, staff, datetime, status, notes.

---

## 8. Feature Summary (Short)

* Core CRM: leads, customers, deals, appointments, tasks, interaction logs.  
* Sales Tools: lead scoring, pipelines, product linking.  
* Support Tools: ticketing + SLA, escalation, support analytics.  
* AI: lead matching, recommendation engine.  
* Admin Tools: multi-tenancy, RBAC, onboarding, audit logs, analytics.  
* Customer Portal: tickets, appointments, profile, documents.

---

If you want next: I can generate architecture diagram, folder structure, API docs, ERD, or flowcharts (lead conversion, ticket lifecycle, login).

