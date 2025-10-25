
# Proyecto de Microservicios Bancarios

## Descripción General
Este proyecto implementa un sistema bancario distribuido mediante **microservicios**, desarrollado con **Spring Boot 3.5.6**, **Java 21**, y **Maven**.  
Está compuesto por dos servicios principales:

- **Microservicio Customers:** gestión de clientes y publicación de eventos en RabbitMQ.  
- **Microservicio Accounts:** gestión de cuentas, transacciones y generación de reportes.  

Cada microservicio posee su propia base de datos MySQL y se comunica de forma asíncrona utilizando **RabbitMQ**.  
La orquestación de los servicios se realiza mediante **Docker Compose**.

---

## Tecnologías Utilizadas
- **Java 21 (LTS)**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring Validation**
- **Spring AMQP (RabbitMQ)**
- **MySQL / MariaDB**
- **Docker & Docker Compose**
- **JUnit 5 / Mockito**
- **Maven 3.9.9**

---

## Arquitectura del Proyecto

### Estructura General
```
banking-microservices/
 ├── customers/
 │    ├── src/main/java/com/bank/customers/
 │    ├── Dockerfile
 │    └── pom.xml
 ├── accounts/
 │    ├── src/main/java/com/bank/accounts/
 │    ├── Dockerfile
 │    └── pom.xml
 ├── docker-compose.yml
 └── README.md
```

Cada microservicio se ejecuta de forma independiente y expone su propia API REST.  
La comunicación entre ellos se realiza mediante eventos RabbitMQ.

---

## 1. Microservicio Customers

### Funcionalidad
- Creación, actualización, eliminación y consulta de clientes.
- Publicación de eventos a RabbitMQ cuando se crea un nuevo cliente.
- Persistencia en base de datos MySQL dedicada.

### Estructura de Paquetes
```
com.bank.customers
 ├── config
 ├── controller
 ├── events
 ├── model
 ├── repository
 ├── service
 └── CustomersApplication.java
```

### Lógica de Negocio
El método principal de creación genera un UUID, guarda el cliente y publica un evento:

```java
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final AmqpTemplate amqpTemplate;

    @Transactional
    public Customer create(Customer customer) {
        customer.setCustomerId(UUID.randomUUID().toString());
        Customer saved = repository.save(customer);

        CustomerEvent event = new CustomerEvent(saved.getCustomerId(), saved.getName(), saved.getStatus());
        amqpTemplate.convertAndSend("customers.exchange", "customer.created", event);

        return saved;
    }
}
```

---

## 2. Microservicio Accounts

### Funcionalidad
- Creación de cuentas asociadas a un cliente.
- Registro de transacciones (depósitos y retiros).
- Validación de saldo disponible.
- Generación de reportes financieros por rango de fechas.

### Estructura de Paquetes
```
com.bank.accounts
 ├── config
 ├── controller
 ├── exception
 ├── listener
 ├── model
 ├── repository
 ├── service
 └── AccountsApplication.java
```

### Lógica de Negocio
El método `register` maneja los movimientos y valida saldos:

```java
@Transactional
public Transaction register(String accountNumber, TransactionType type, BigDecimal amount) {
    Account account = accountRepo.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new NotFoundException("Account not found"));

    BigDecimal newBalance = switch (type) {
        case DEPOSIT -> account.getInitialBalance().add(amount);
        case WITHDRAWAL -> {
            if (account.getInitialBalance().compareTo(amount) < 0)
                throw new InsufficientBalanceException("Insufficient balance");
            yield account.getInitialBalance().subtract(amount);
        }
    };

    account.setInitialBalance(newBalance);
    accountRepo.save(account);

    Transaction tx = new Transaction(UUID.randomUUID().toString(), account, type, amount, newBalance);
    return txRepo.save(tx);
}
```

---

## Comunicación con RabbitMQ
El intercambio de mensajes se realiza mediante un **topic exchange**.

- **Customers Service** publica el evento:
  - Exchange: `customers.exchange`
  - Routing Key: `customer.created`
- **Accounts Service** puede escuchar este evento para sincronizar datos del cliente.

---

## Configuración Docker

### Ejemplo Dockerfile (Customers)
```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar customers.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/customers.jar"]
```

### Docker Compose
Archivo `docker-compose.yml` incluye:
- **customers-service**  
- **accounts-service**  
- **MySQL** (dos contenedores independientes)  
- **RabbitMQ**

Cada servicio tiene su puerto expuesto:
- Customers: `8081`
- Accounts: `8082`
- RabbitMQ Dashboard: `15672` (usuario: `guest`, contraseña: `guest`)

---

## Pruebas Unitarias

Se implementaron pruebas con **JUnit 5 y Mockito** para validar la lógica de negocio sin levantar el contexto de Spring.

### CustomerServiceTest
Valida que:
- El cliente se guarde correctamente.
- Se publique un evento en RabbitMQ.

```java
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private AmqpTemplate amqpTemplate;

    @InjectMocks
    private CustomerService service;

    @Test
    void create_shouldSaveCustomerAndPublishEvent() {
        Customer customer = new Customer();
        customer.setName("Jose Lema");
        customer.setStatus(true);

        when(repository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setCustomerId(UUID.randomUUID().toString());
            return c;
        });

        Customer saved = service.create(customer);

        assertNotNull(saved.getCustomerId());
        verify(repository, times(1)).save(any(Customer.class));
        verify(amqpTemplate, times(1))
            .convertAndSend(eq("customers.exchange"), eq("customer.created"), any(CustomerEvent.class));
    }
}
```

---

## Ejecución del Proyecto

### Compilar y Construir
```bash
mvn clean package -DskipTests
```

### Levantar con Docker
```bash
docker-compose up --build
```

### Acceso a los Servicios
| Servicio | URL |
|-----------|-----|
| Customers | http://localhost:8081 |
| Accounts  | http://localhost:8082 |
| RabbitMQ  | http://localhost:15672 |

### Ejecutar Pruebas
```bash
mvn test
```

---

## Autor
**Antony Chávez**  
Entorno de desarrollo:  
Windows 11 • Java 21 • Maven 3.9.9 • Docker 28.0.4 • Spring Boot 3.5.6
