# MultiCore CRM - Postman API Documentation

**Base URL:** `http://localhost:8080`

**Authentication:** Most endpoints require JWT token in Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

---

## 🔐 Authentication Endpoints (Public - No Auth Required)

---

### 2. Register Customer

**POST** `/api/auth/register/customer`

**Request Body:**

```json
{
  "fullName": "John Customer",
  "email": "customer@example.com",
  "password": "customer123",
  "phone": "+1234567890"
}
```

**Response:** Returns JWT token + user details

---

### 3. Login (All User Types)

**POST** `/api/auth/login`

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "User Name",
  "businessId": 1,
  "role": "SUPER_ADMIN",
  "message": "Login successful",
  "success": true
}
```

---

## 👑 Super Admin Endpoints (Requires: SUPER_ADMIN role)

### 4. Create Business (Tenant)

**POST** `/api/admin/create-business`

**Headers:** `Authorization: Bearer <super_admin_token>`

**Request Body:**

```json
{
  "name": "Acme Corporation",
  "description": "Software development company",
  "industry": "Technology"
}
```

---

### 5. Create Business Owner

**POST** `/api/admin/create-owner`

**Headers:** `Authorization: Bearer <super_admin_token>`

**Request Body:**

```json
{
  "fullName": "Business Owner",
  "email": "owner@acme.com",
  "password": "owner123",
  "phone": "+1234567890",
  "businessId": 1
}
```

---

### 6. Get All Businesses

**GET** `/api/admin/businesses`

**Headers:** `Authorization: Bearer <super_admin_token>`

---

### 7. Get Business by ID

**GET** `/api/admin/businesses/{businessId}`

**Headers:** `Authorization: Bearer <super_admin_token>`

**Example:** `/api/admin/businesses/1`

---

### 8. Activate/Deactivate Business

**PATCH** `/api/admin/businesses/{businessId}/status?active=true`

**Headers:** `Authorization: Bearer <super_admin_token>`

**Query Parameters:**

- `active` (boolean): `true` to activate, `false` to deactivate

**Example:** `/api/admin/businesses/1/status?active=false`

---

### 9. Platform Statistics

**GET** `/api/admin/stats`

**Headers:** `Authorization: Bearer <super_admin_token>`

**Response:**

```json
{
  "totalBusinesses": 10,
  "activeBusinesses": 8,
  "inactiveBusinesses": 2,
  "totalUsers": 150,
  "totalLeads": 500,
  "totalCustomers": 300
}
```

---

## 🏢 Business Admin (Owner) Endpoints (Requires: BUSINESS_ADMIN role)

### 10. Create Staff Member

**POST** `/api/owner/create-staff`

**Headers:** `Authorization: Bearer <owner_token>`

**Query Parameters:**

- `fullName` (string): Staff full name
- `email` (string): Staff email
- `password` (string): Staff password
- `phone` (string): Staff phone
- `role` (enum): One of: `SALES_MANAGER`, `SALES_AGENT`, `SUPPORT_MANAGER`, `SUPPORT_AGENT`, `FINANCE`, `VIEWER`

**Example:** `/api/owner/create-staff?fullName=John%20Sales&email=sales@acme.com&password=sales123&phone=+1234567890&role=SALES_AGENT`

---

## 👥 Customer Management (Requires: BUSINESS_ADMIN, SALES_MANAGER, SALES_AGENT, SUPPORT_MANAGER, SUPPORT_AGENT, FINANCE, VIEWER)

### 11. Create Customer

**POST** `/api/customers`

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "businessId": 1,
  "name": "Acme Customer",
  "email": "customer@acme.com",
  "phone": "+1234567890",
  "address": "123 Main St",
  "source": "Website"
}
```

---

### 12. Get All Customers

**GET** `/api/customers`

**Headers:** `Authorization: Bearer <token>`

---

### 13. Get Customer by ID

**GET** `/api/customers/{id}`

**Headers:** `Authorization: Bearer <token>`

**Example:** `/api/customers/1`

---

### 14. Update Customer

**PUT** `/api/customers/{id}`

**Headers:** `Authorization: Bearer <token>`

**Request Body:** Same as Create Customer

---

### 15. Delete Customer

**DELETE** `/api/customers/{id}`

**Headers:** `Authorization: Bearer <token>`

---

## 📋 Lead Management (Requires: BUSINESS_ADMIN, SALES_MANAGER, SALES_AGENT, VIEWER)

### 16. Create Lead

**POST** `/api/leads`

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "businessId": 1,
  "customerId": null,
  "assignedToId": 2,
  "name": "John Lead",
  "email": "lead@example.com",
  "phone": "+1234567890",
  "company": "Lead Company",
  "jobTitle": "Manager",
  "notes": "Interested in our product"
}
```

---

### 17. Get All Leads for Business

**GET** `/api/leads/business/{businessId}`

**Headers:** `Authorization: Bearer <token>`

**Example:** `/api/leads/business/1`

---

### 18. Get Lead by ID

**GET** `/api/leads/{id}`

**Headers:** `Authorization: Bearer <token>`

**Example:** `/api/leads/1`

---

### 19. Update Lead

**PUT** `/api/leads/{id}`

**Headers:** `Authorization: Bearer <token>`

**Request Body:** Same structure as Create Lead (all fields optional)

---

### 20. Delete Lead

**DELETE** `/api/leads/{id}`

**Headers:** `Authorization: Bearer <token>`

---

### 21. Update Lead Status

**PATCH** `/api/leads/{id}/status?status=CONTACTED`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**

- `status` (string): `NEW`, `CONTACTED`, `QUALIFIED`, `LOST`

**Example:** `/api/leads/1/status?status=QUALIFIED`

---

### 22. Update Lead Score

**PATCH** `/api/leads/{id}/score?score=85`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**

- `score` (integer): Lead score (0-100)

---

### 23. Convert Lead to Customer

**POST** `/api/leads/{id}/convert`

**Headers:** `Authorization: Bearer <token>`

**Response:** Returns updated LeadDTO with customerId set

---

### 24. Filter Leads

**GET** `/api/leads/filter?businessId=1&status=NEW&minScore=50&maxScore=100`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters (all optional):**

- `businessId` (long)
- `status` (enum): `NEW`, `CONTACTED`, `QUALIFIED`, `LOST`
- `minScore` (integer)
- `maxScore` (integer)

---

### 25. Search Leads by Name

**GET** `/api/leads/search?name=John`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**

- `name` (string): Search term

---

## 🎫 Ticket Management (Requires: BUSINESS_ADMIN, SUPPORT_MANAGER, SUPPORT_AGENT, VIEWER, CUSTOMER)

### 26. Create Ticket

**POST** `/api/tickets`

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "businessId": 1,
  "title": "Login Issue",
  "description": "Cannot login to the system",
  "priority": "HIGH",
  "assignedToUserId": 5
}
```

**Priority Values:** `LOW`, `MEDIUM`, `HIGH`, `URGENT`

---

### 27. List Tickets (with Pagination & Filters)

**GET** `/api/tickets/business/{businessId}?page=0&size=20&status=OPEN&priority=HIGH&assignedTo=5`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters (all optional):**

- `page` (int, default: 0): Page number
- `size` (int, default: 20): Page size
- `status` (enum): `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`
- `priority` (enum): `LOW`, `MEDIUM`, `HIGH`, `URGENT`
- `assignedTo` (long): User ID of assignee

**Example:** `/api/tickets/business/1?page=0&size=10&status=OPEN`

---

### 28. Get Ticket by ID

**GET** `/api/tickets/{ticketId}`

**Headers:** `Authorization: Bearer <token>`

**Example:** `/api/tickets/1`

---

### 29. Update Ticket Status

**PATCH** `/api/tickets/{ticketId}/status`

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "status": "IN_PROGRESS"
}
```

**Status Values:** `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`

---

### 30. Assign Ticket

**PATCH** `/api/tickets/{ticketId}/assign?assigneeId=5`

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**

- `assigneeId` (long): User ID to assign ticket to

---

### 31. Add Comment to Ticket

**POST** `/api/tickets/{ticketId}/comments`

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "comment": "Working on this issue. Will update soon."
}
```

---

### 32. Escalate Ticket

**POST** `/api/tickets/{ticketId}/escalate`

**Headers:** `Authorization: Bearer <token>`

**Response:** Ticket escalated to URGENT priority with new SLA due date

---

### 33. Get Ticket Analytics

**GET** `/api/tickets/analytics/{businessId}`

**Headers:** `Authorization: Bearer <token>`

**Response:**

```json
{
  "total": 100,
  "open": 25,
  "inProgress": 15,
  "resolved": 40,
  "closed": 20,
  "low": 30,
  "medium": 40,
  "high": 20,
  "urgent": 10
}
```

---

## 📝 Testing Workflow

### Step 1: Register Super Admin (One-time)

```
POST /api/auth/register/admin
Body: { "fullName": "Admin", "email": "admin@test.com", "password": "admin123", "phone": "+1234567890" }
```

### Step 2: Login as Super Admin

```
POST /api/auth/login
Body: { "email": "admin@test.com", "password": "admin123" }
Save the token!
```

### Step 3: Create Business

```
POST /api/admin/create-business
Headers: Authorization: Bearer <admin_token>
Body: { "name": "Test Business", "description": "Test", "industry": "Tech" }
Save businessId!
```

### Step 4: Create Owner for Business

```
POST /api/admin/create-owner
Headers: Authorization: Bearer <admin_token>
Body: { "fullName": "Owner", "email": "owner@test.com", "password": "owner123", "phone": "+1234567890", "businessId": 1 }
Save owner token!
```

### Step 5: Login as Owner

```
POST /api/auth/login
Body: { "email": "owner@test.com", "password": "owner123" }
Save owner token!
```

### Step 6: Create Staff (as Owner)

```
POST /api/owner/create-staff?fullName=Sales%20Agent&email=sales@test.com&password=sales123&phone=+1234567890&role=SALES_AGENT
Headers: Authorization: Bearer <owner_token>
```

### Step 7: Create Customer

```
POST /api/customers
Headers: Authorization: Bearer <owner_token>
Body: { "businessId": 1, "name": "Customer", "email": "customer@test.com", "phone": "+1234567890" }
```

### Step 8: Create Lead

```
POST /api/leads
Headers: Authorization: Bearer <owner_token>
Body: { "businessId": 1, "name": "Lead", "email": "lead@test.com", "phone": "+1234567890" }
```

### Step 9: Create Ticket

```
POST /api/tickets
Headers: Authorization: Bearer <owner_token>
Body: { "businessId": 1, "title": "Test Ticket", "description": "Test issue", "priority": "MEDIUM" }
```

---

## 🔑 Role Reference

- **SUPER_ADMIN**: Platform-wide control
- **BUSINESS_ADMIN**: Business owner/admin
- **SALES_MANAGER**: Manages sales team
- **SALES_AGENT**: Works on assigned leads
- **SUPPORT_MANAGER**: Oversees support
- **SUPPORT_AGENT**: Handles tickets
- **FINANCE**: Billing and invoices
- **VIEWER**: Read-only access
- **CUSTOMER**: External customer portal

---

## ⚠️ Important Notes

1. **JWT Token**: Extract token from login response and use in `Authorization: Bearer <token>` header
2. **Business Isolation**: All data is scoped to businessId from JWT token
3. **Role-Based Access**: Each endpoint requires specific roles
4. **Pagination**: Ticket list endpoint supports pagination (page, size)
5. **SLA Breach**: SLA breach checker runs every 5 minutes automatically
6. **Tenant Isolation**: Users can only access data from their own business

---

## 🧪 Postman Collection Setup

1. Create environment variables:

   - `base_url`: `http://localhost:8080`
   - `admin_token`: (set after admin login)
   - `owner_token`: (set after owner login)
   - `business_id`: (set after creating business)

2. Use Pre-request Scripts to auto-set Authorization header:

```javascript
pm.request.headers.add({
  key: "Authorization",
  value: "Bearer " + pm.environment.get("admin_token"),
});
```

3. Use Tests to extract tokens:

```javascript
var jsonData = pm.response.json();
if (jsonData.token) {
  pm.environment.set("admin_token", jsonData.token);
}
```
