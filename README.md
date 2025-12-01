*Backend dev technical test*

# Similar Products

Spring Boot application that provides information about similar products to a given one.

## Description

This application exposes a REST service that allows querying similar products and retrieving their details concurrently, using Java 21 Virtual Threads to optimize performance.

## Technologies

- **Java 21** - Leveraging Virtual Threads
- **Spring Boot 3.5.7**
- **Spring WebFlux** - Reactive HTTP client
- **Resilience4j** - Circuit Breaker and Retry
- **Maven** - Dependency management
- **Lombok** - Boilerplate code reduction

## Features

- Query similar products with concurrent processing
- Error handling with custom exceptions
- Circuit Breaker for failure protection
- Configurable automatic retries
- Automatic removal of duplicate IDs
- Virtual Threads for high concurrency

## Configuration

The application is configured via `application.yml`:

```yaml
server:
  port: 5000

rest-client:
  connect-timeout: 5s
  read-timeout: 60s
  product-base-url: http://localhost:3001
```

### Circuit Breaker

- **similarProducts**: 20 requests, 50% failure rate, 5s in open state
- **productDetails**: 50 requests, 50% failure rate, 10s in open state

### Retry

- **similarProductsRetry**: 2 attempts, 200ms between attempts
- **productDetailsRetry**: 3 attempts, 300ms between attempts

## Execution

```bash
mvn spring-boot:run
```

## Testing
```bash
mvn test
```

## Project Structure

```
src/main/java/dev/juliangarcia/similarproducts/
├── application/
│   ├── provider/          # Provider interfaces
│   └── usecase/           # Use cases
├── domain/
│   ├── entity/            # Domain entities
│   ├── exception/         # Business exceptions
│   └── repository/        # Repository interfaces
└── infrastructure/
    ├── controller/        # REST controllers
    └── repository/        # Repository implementations
```

## API Endpoint

```
GET /product/{productId}/similar
```
Returns a list of similar products with their complete details.

## Possible Improvements

- **API Documentation**: Integrate OpenAPI for automatic REST endpoint documentation
- **OpenAPI Generation**: Generate REST clients and DTOs automatically from OpenAPI definitions using `openapi-generator-maven-plugin`
- **Caching**: Implement a cache layer (Redis, Caffeine) to reduce external API calls for frequently requested products
- **Observability**: Add distributed tracing, metrics dashboards, and enhanced structured logging with correlation IDs for better troubleshooting
- **Rate Limiting**: Implement rate limiting to prevent API abuse and protect external services
- **Security**: Implement authentication and authorization (OAuth2, JWT)
- **MapStruct**: Replace manual mappers with MapStruct for compile-time safe and efficient object mapping
- **Fully Reactive Architecture**: Migrate to end-to-end reactive programming with Project Reactor, eliminating Virtual Threads and `.block()` calls for maximum efficiency (with the trade-offs mentioned in Key Design Decisions)

## Key Design Decision

### Hybrid Reactive-Synchronous Architecture

The application uses a **hybrid approach** combining reactive infrastructure with synchronous business logic:

- **Infrastructure Layer**: Uses Spring WebFlux's reactive `WebClient` for non-blocking HTTP calls
- **Application/Domain Layers**: Maintains synchronous, imperative code for business logic
- **Concurrency**: Java 21 Virtual Threads handle concurrent operations via `CompletableFuture`

**Why this approach?**

- **Layer Decoupling**: Keeps domain and application layers independent from reactive infrastructure concerns (Reactor's `Mono`/`Flux`)
- **Testability**: Synchronous business logic is easier to test without complex reactive test setups
- **Maintainability**: Team members can understand and modify business logic without deep reactive programming knowledge
- **Performance**: Virtual Threads provide excellent concurrency without thread-blocking overhead

**Trade-off**: A fully reactive architecture (eliminating `.block()` and Virtual Threads) would theoretically offer better resource utilization and backpressure handling, but at the cost of:
- Tighter coupling between layers
- Increased complexity in business logic
- Steeper learning curve for the team
- More complex testing scenarios

This hybrid approach prioritizes **code clarity and maintainability** while still achieving good performance through Virtual Threads.

## Design Highlights

Special effort has been made to ensure:

- **Code Clarity and Maintainability**
  - Hexagonal architecture with clear separation of concerns (domain, application, infrastructure)
  - Small, focused classes with single responsibilities
  - Descriptive naming and comprehensive test coverage
  - Minimal use of frameworks in domain layer for better testability

- **Performance**
  - Java 21 Virtual Threads for high-concurrency processing of product details
  - Concurrent fetching of multiple product details without blocking threads
  - Efficient deduplication of similar product IDs before processing
  - Reactive HTTP client (WebFlux) for non-blocking I/O operations

- **Resilience**
  - Circuit Breaker pattern to prevent cascading failures
  - Automatic retry mechanisms with configurable backoff
  - Comprehensive error handling with custom domain exceptions
  - Timeouts configured for all external service calls
  - Graceful degradation when external services fail