# REST Clients and Eureka Service Discovery

## REST Client Styles

```text
RestTemplate → Method-based API
WebClient    → Fluent / Chained API
Feign        → Annotation-based API
```

---

## HTTP Request Using Different Clients

```text
                         HTTP Request
                              │
             ┌────────────────┼────────────────┐
             │                │                │
        RestTemplate       WebClient         Feign
             │                │                │
          Methods        Chained Methods    Annotations
             │                │                │
       getForObject()        .get()        @GetMapping
       postForObject()       .post()       @PostMapping
       put()                 .put()         @PutMapping
       delete()              .delete()      @DeleteMapping
       exchange()            .retrieve()
```

### Easy Way to Remember

```text
RestTemplate → Methods

WebClient    → Chained / Fluent Methods

Feign        → Annotations
```

---

# Eureka and Service Discovery

## Important Terms

### Eureka / Discovery Server

**Eureka Server** maintains the **service registry** and provides **service-discovery information** to clients.

```text
Eureka Server
      │
      └── Maintains Service Registry
```

---

### Service Registry

The **Service Registry** is the stored list of:

* Service names
* Service instances
* IP addresses / hostnames
* Ports
* Instance status

Example:

```text
Service Registry
────────────────────────────
CUSTOMER-SERVICE
    ↓
localhost:8090

ACCOUNT-SERVICE
    ↓
localhost:8091
```

So:

```text
Service Registry
       ↓
Stores service names + locations
```

---

### Service Discovery

**Service Discovery** is the **process of finding where a particular service is running**.

For example:

```text
Account Service
      │
      │ "Where is CUSTOMER-SERVICE?"
      ↓
Eureka Server
      │
      ↓
CUSTOMER-SERVICE → localhost:8090
```

So:

```text
Service Discovery
       ↓
Process of finding a service
```

---

### Eureka Client

A **Eureka Client** is an application that participates in service discovery.

It can:

* Register itself with Eureka
* Discover other services through Eureka

For example:

```text
Account Service
       │
       ├── Registers itself
       │
       └── Discovers CUSTOMER-SERVICE
                    │
                    ↓
               Eureka Server
```

---

## @EnableDiscoveryClient

```java
```

This annotation enables an application to participate in **service discovery**.

In simple terms:

```text
@EnableDiscoveryClient
        ↓
Application participates in
service discovery
        ↓
Can register/discover services
```

> **Note:** With modern Spring Cloud setups, explicit `@EnableDiscoveryClient` is often not required when the appropriate discovery client dependency and configuration are present. However, the annotation is still useful for understanding the concept.

---

# How Discovery Happens

Suppose we have two microservices:

```text
Account Service
      │
      │
      ↓
Customer Service
```

Account Service needs to call Customer Service.

Instead of hardcoding the Customer Service location, Account Service can use **service discovery**.

---

## Step 1: Services Register with Eureka

When the services start, they register themselves with Eureka.

For example:

```text
Customer Service
       │
       │ Register
       ↓
Eureka Server
```

Eureka stores:

```text
CUSTOMER-SERVICE → localhost:8090
```

Similarly:

```text
ACCOUNT-SERVICE → localhost:8091
```

The Eureka registry may therefore contain:

```text
Service Registry
────────────────────────────

CUSTOMER-SERVICE
    ↓
localhost:8090

ACCOUNT-SERVICE
    ↓
localhost:8091
```

---

# Step 2: Account Service Needs Customer Service

Suppose Account Service needs customer information for customer ID `10`.

It needs to call:

```text
CUSTOMER-SERVICE
```

But Account Service does not necessarily need to know the actual host and port beforehand.

It asks Eureka for the location of Customer Service.

```text
Account Service
      |
      | "Give me an instance of CUSTOMER-SERVICE"
      ↓
Eureka Server
```

---

# Step 3: Eureka Checks the Registry

Eureka checks the service registry.

For example:

```text
CUSTOMER-SERVICE
       ↓
localhost:8090
```

Eureka returns the available instance information.

So the **discovery result** is essentially:

```text
CUSTOMER-SERVICE
       ↓
localhost:8090
```

If multiple instances are running, Eureka can provide multiple instances:

```text
CUSTOMER-SERVICE
       │
       ├── localhost:8090
       ├── localhost:8092
       └── localhost:8093
```

---

# Step 4: Account Service Makes the Actual Request

After obtaining the service instance information, the actual HTTP request can be sent to the Customer Service instance.

For example:

```text
http://localhost:8090/customers/10
```

The important point is:

```text
Eureka
  ↓
Discovers WHERE the service is

Customer Service
  ↓
Actually processes the HTTP request
```

Eureka is **not the service that processes the customer request**.

---

# Complete Discovery Flow

```text
                    Account Service
                          │
                          │
                          │ 1. Need CUSTOMER-SERVICE
                          ↓
                    Eureka Server
                          │
                          │ 2. Check Registry
                          ↓
                  Service Registry
                          │
                          │
                  CUSTOMER-SERVICE
                          │
                          ↓
                    localhost:8090
                          │
                          │ 3. Return instance
                          ↓
                    Account Service
                          │
                          │ 4. HTTP Request
                          ↓
             http://localhost:8090/customers/10
                          │
                          ↓
                  Customer Service
```

---

# Eureka vs Service Registry vs Service Discovery

These terms are related but they are **not exactly the same thing**.

| Term                  | Meaning                                                                   |
| --------------------- | ------------------------------------------------------------------------- |
| **Eureka Server**     | The server that maintains the registry and provides discovery information |
| **Service Registry**  | The stored information about registered service instances                 |
| **Service Discovery** | The process of finding a service instance                                 |
| **Eureka Client**     | Application that registers with and/or discovers services through Eureka  |
| **Load Balancer**     | Selects an instance when multiple service instances are available         |

---

# Where Load Balancer Comes In

Suppose Eureka has multiple instances:

```text
CUSTOMER-SERVICE
       │
       ├── localhost:8090
       ├── localhost:8092
       └── localhost:8093
```

The Load Balancer can select one of those instances.

```text
Account Service
      │
      │ Request for CUSTOMER-SERVICE
      ↓
Load Balancer
      │
      │ Gets available instances
      ↓
Eureka / Discovery Information
      │
      ↓
┌─────────────────────────┐
│ CUSTOMER-SERVICE        │
│                         │
│ localhost:8090          │
│ localhost:8092          │
│ localhost:8093          │
└─────────────────────────┘
      │
      │ Select one instance
      ↓
Customer Service Instance
```

So the responsibilities can be remembered as:

```text
Eureka
  ↓
"Where are the service instances?"

Service Registry
  ↓
"Here are the registered instances."

Service Discovery
  ↓
"Find the required service."

Load Balancer
  ↓
"Which available instance should receive the request?"
```

---

# Final Simple Picture

```text
                    ┌───────────────────┐
                    │   Eureka Server   │
                    │                   │
                    │ Service Registry  │
                    └─────────┬─────────┘
                              │
                    Service Discovery
                              │
              ┌───────────────┴───────────────┐
              │                               │
              ↓                               ↓
       Account Service                 Customer Service
          localhost:8091                  localhost:8090
              │
              │
              │ Needs CUSTOMER-SERVICE
              ↓
        Discover service
              │
              ↓
         Eureka Server
              │
              ↓
      CUSTOMER-SERVICE
        localhost:8090
              │
              │ Actual HTTP request
              ↓
      Customer Service
```

## One-Line Summary

```text
Eureka Server → Maintains the registry

Service Registry → Stores service instances

Service Discovery → Finds the required service

Eureka Client → Application participating in discovery

Load Balancer → Selects an instance

HTTP Client → Sends the actual HTTP request
```
