# Comparison of the Clients

In Spring Boot microservices, we commonly use three clients for service-to-service communication:

1. **RestTemplate**
2. **WebClient**
3. **Feign Client**

## RestTemplate vs WebClient vs Feign

| Feature                        | RestTemplate                          | WebClient                                | Feign                                                         |
| ------------------------------ | ------------------------------------- | ---------------------------------------- | ------------------------------------------------------------- |
| **Style**                      | Method-based                          | Fluent API                               | Interface + Annotations                                       |
| **GET**                        | `getForObject()` / `getForEntity()`   | `.get()`                                 | `@GetMapping`                                                 |
| **POST**                       | `postForObject()` / `postForEntity()` | `.post()`                                | `@PostMapping`                                                |
| **PUT**                        | `put()`                               | `.put()`                                 | `@PutMapping`                                                 |
| **DELETE**                     | `delete()`                            | `.delete()`                              | `@DeleteMapping`                                              |
| **Headers**                    | `HttpHeaders` / `HttpEntity`          | `.header()` / `.headers()`               | `@RequestHeader`                                              |
| **Request Body**               | `HttpEntity`                          | `.bodyValue()` / `.body()`               | `@RequestBody`                                                |
| **Response**                   | `ResponseEntity` / Object             | `Mono` / `Flux` / `ResponseEntity`       | Method return type                                            |
| **Reactive**                   | ❌ No                                  | ✅ Yes                                    | ❌ No                                                          |
| **Blocking**                   | ✅ Yes                                 | ⚠️ Can be made blocking using `.block()` | ✅ Normally synchronous                                        |
| **Eureka / Service Discovery** | ✅ With Load Balancer                  | ✅ With Load Balancer                     | ✅ Commonly integrated with Service Discovery / Load Balancing |

---

## 1. RestTemplate

`RestTemplate` is a **synchronous and blocking** HTTP client.

### Example

```java
CustomerResponse response =
        restTemplate.getForObject(
                "http://customer-service/customers/{id}",
                CustomerResponse.class,
                id
        );
```

The current thread **waits until the response is received**.

### Flow

```text
Account Service
       |
       | HTTP Request
       ↓
Customer Service
       |
       | HTTP Response
       ↓
Account Service
       |
       ↓
Continue Execution
```

---

## 2. WebClient

`WebClient` is a **reactive and non-blocking** HTTP client.

### Example

```java
Mono<CustomerResponse> response =
        webClient.get()
                .uri("http://customer-service/customers/{id}", id)
                .retrieve()
                .bodyToMono(CustomerResponse.class);
```

Here, `Mono<CustomerResponse>` represents the response that will be available asynchronously.

### Making WebClient Blocking

If we use `.block()`:

```java
CustomerResponse response =
        webClient.get()
                .uri("http://customer-service/customers/{id}", id)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .block();
```

Then the current thread **waits for the response**.

So:

```text
WebClient without .block()
        ↓
Non-blocking / Reactive

WebClient with .block()
        ↓
Blocking
```

---

## 3. Feign Client

Feign is a **declarative HTTP client**.

Instead of manually building the HTTP request, we define an interface using annotations.

### Example

```java
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customers/{id}")
    CustomerResponse getCustomerById(@PathVariable Long id);
}
```

Then we can simply call:

```java
CustomerResponse response =
        customerClient.getCustomerById(id);
```

Feign handles the HTTP communication internally.

### Flow

```text
Account Service
       |
       | customerClient.getCustomerById(id)
       ↓
Feign Client
       |
       | Service Discovery
       ↓
Load Balancer
       |
       ↓
Customer Service
       |
       | Response
       ↓
Account Service
```

---

# Key Difference

The main difference is **how we write the code for making HTTP calls**.

```text
RestTemplate
     ↓
Method-based API
     ↓
You directly call methods such as
getForObject(), postForObject(), put(), delete()

WebClient
     ↓
Fluent API
     ↓
You build the request using
.get(), .post(), .put(), .delete()

Feign
     ↓
Interface + Annotations
     ↓
You define what the HTTP call should look like
and Feign handles the communication
```

---

# Easy Way to Remember

```text
RestTemplate → Method-based + Blocking

WebClient    → Fluent API + Reactive + Non-blocking

Feign        → Interface + Annotations + Declarative
```

## One-Line Summary

| Client           | Simple Meaning                                         |
| ---------------- | ------------------------------------------------------ |
| **RestTemplate** | Call another service using predefined HTTP methods     |
| **WebClient**    | Build HTTP calls using a fluent reactive API           |
| **Feign**        | Define an interface and let Feign handle the HTTP call |
