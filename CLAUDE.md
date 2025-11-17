# BoardPractice - AI Assistant Guide

## Project Overview

**Project Name**: BoardPractice (게시판 연습)
**Type**: Spring Boot Web Application (Bulletin Board System)
**Language**: Java 1.8
**Framework**: Spring Boot 2.7.2
**Build Tool**: Gradle 7.5
**Primary Language**: Korean (comments and UI text)

This is a full-stack web application demonstrating a bulletin board system with:
- Post CRUD operations with pagination and search
- JWT-based security
- WebSocket/STOMP messaging
- Eureka service discovery
- Excel export functionality
- MyBatis for database access

## Codebase Structure

```
BoardPractice/
├── build.gradle                    # Gradle build configuration
├── settings.gradle                 # Project name: "Board"
├── gradlew & gradlew.bat          # Gradle wrapper scripts
└── src/
    ├── main/
    │   ├── java/com/study/
    │   │   ├── BoardApplication.java          # Main Spring Boot entry point
    │   │   ├── ExcelController.java          # Excel export API
    │   │   ├── TestController.java           # Test endpoints
    │   │   ├── aop/
    │   │   │   └── LogAll.java              # AOP logging aspect
    │   │   ├── common/dto/                  # Shared DTOs
    │   │   │   ├── CommonDto.java
    │   │   │   ├── GenericResponse.java
    │   │   │   ├── MessageDto.java
    │   │   │   ├── PasswordDto.java
    │   │   │   └── SearchDto.java           # Search parameters
    │   │   ├── config/                      # Spring configuration
    │   │   │   ├── DatabaseConfig.java     # MyBatis & HikariCP
    │   │   │   ├── ServiceConfigure.java
    │   │   │   ├── WebMvcConfig.java       # Interceptors
    │   │   │   └── WebSocketConfig.java    # WebSocket/STOMP
    │   │   ├── domain/post/                # Post domain (main feature)
    │   │   │   ├── PostController.java     # MVC controller
    │   │   │   ├── PostRestController.java # REST API
    │   │   │   ├── PostService.java        # Business logic
    │   │   │   ├── PostMapper.java         # MyBatis interface
    │   │   │   ├── PostRequest.java        # Request DTO
    │   │   │   └── PostResponse.java       # Response DTO
    │   │   ├── exception/
    │   │   │   └── UserNotFoundException.java
    │   │   ├── filter/                     # Servlet filters
    │   │   │   ├── TestFilter.java
    │   │   │   └── TransactionFilter.java
    │   │   ├── interceptor/
    │   │   │   └── LoggerInterceptor.java
    │   │   ├── paging/                     # Pagination utilities
    │   │   │   ├── Pagination.java
    │   │   │   └── PagingResponse.java
    │   │   ├── security/                   # Security & JWT
    │   │   │   ├── Jwt.java
    │   │   │   └── WebSecurityConfigure.java
    │   │   ├── services/
    │   │   │   └── SortService.java        # Stream API examples
    │   │   └── socket/
    │   │       └── StompHandler.java       # WebSocket handler
    │   └── resources/
    │       ├── application.properties      # Main configuration
    │       ├── log4jdbc.log4j2.properties # SQL logging
    │       ├── logback-spring.xml         # Logging config
    │       ├── mappers/
    │       │   └── PostMapper.xml         # MyBatis SQL queries
    │       ├── static/                    # CSS, JS, fonts
    │       └── templates/                 # Thymeleaf HTML templates
    └── test/
        └── java/com/study/
            ├── BoardApplicationTests.java
            ├── PostMapperTest.java       # Mapper tests
            ├── PostServiceTest.java      # Service tests
            └── services/
                └── SortServiceTest.java  # JUnit 5 examples
```

## Architecture & Design Patterns

### Layered Architecture
- **Controller Layer**: MVC controllers and REST API controllers
- **Service Layer**: Business logic (e.g., `PostService`)
- **Data Access Layer**: MyBatis mappers (interface + XML)
- **Domain/DTO Layer**: Request/Response objects, entities

### Key Patterns
- **Domain-Driven Design**: Package by feature (`domain/post/`)
- **DTO Pattern**: Separate Request/Response objects
- **Soft Delete**: Uses `delete_yn` flag instead of hard deletes
- **Repository Pattern**: MyBatis mapper interfaces
- **AOP**: Cross-cutting concerns (logging via `@Aspect`)

## Development Setup

### Prerequisites
- **Java**: JDK 1.8 or higher
- **Database**: MySQL 5.7+ running on `localhost:3306`
- **Database Name**: `board`
- **Database User**: `root` / `zen911!@` (see `application.properties`)

### Database Setup
```sql
-- Create database
CREATE DATABASE board CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- The application expects tables:
-- - tb_post (with columns: id, title, content, writer, view_cnt, notice_yn, delete_yn, created_date, modified_date)
```

### Building the Project
```bash
# Build with Gradle wrapper (recommended)
./gradlew build

# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Skip tests
./gradlew build -x test
```

### Running the Application
```bash
# Using Gradle
./gradlew bootRun

# Using Java
java -jar build/libs/Board-0.0.1-SNAPSHOT.jar
```

**Default Port**: 8080
**Access URL**: http://localhost:8080

### Development Mode Features
- **Hot Reload**: Spring DevTools enabled (auto-restart on code changes)
- **Live Reload**: Browser auto-refresh enabled
- **Thymeleaf Cache**: Disabled for template hot-swapping
- **Static Resources**: Served from `src/main/resources/static/`

## Key Technologies & Dependencies

### Core Spring Boot Starters
- `spring-boot-starter-web` - REST API & MVC
- `spring-boot-starter-data-jpa` - JPA/Hibernate (dependency present but not actively used)
- `spring-boot-starter-thymeleaf` - Server-side templates
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-websocket` - WebSocket support
- `spring-boot-devtools` - Development tools

### Database & Persistence
- **MyBatis** (2.2.2) - SQL mapping framework (primary ORM)
- **HikariCP** - High-performance connection pool
- **MySQL Connector** - JDBC driver
- **Log4JDBC** (1.16) - SQL statement logging with parameters

### Microservices
- **Eureka Server** (3.1.4) - Service discovery (configured but registration disabled)

### Security
- **Spring Security** - Authentication/authorization framework
- **java-jwt** (4.2.1, Auth0) - JWT token creation and validation
- **Jasypt** (3.0.3) - Encryption utilities

### Utilities
- **Lombok** - Boilerplate reduction (`@Getter`, `@Setter`, `@RequiredArgsConstructor`)
- **Apache Commons Lang3** (3.12.0) - String utilities
- **Google Guava** (31.1-jre) - Collections and utilities
- **Apache POI** (3.15) - Excel file generation (`.xls` and `.xlsx`)

### Testing
- **JUnit 5** (Jupiter) - Test framework
- **Spring Boot Test** - `@SpringBootTest` for integration tests
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions

## Coding Conventions

### Java Conventions
1. **Lombok Annotations**: Prefer Lombok over manual getters/setters
   ```java
   @Getter
   @Setter
   @RequiredArgsConstructor
   public class MyClass { ... }
   ```

2. **Naming Conventions**:
   - DTOs: Suffix with `Dto`, `Request`, or `Response`
   - Mappers: Suffix with `Mapper` (interface + XML)
   - Controllers: Suffix with `Controller` or `RestController`
   - Services: Suffix with `Service`

3. **Package Organization**: Group by feature/domain, not layer
   - Good: `domain/post/PostController`, `domain/post/PostService`
   - Avoid: `controller/PostController`, `service/PostService`

4. **Comments**: Primarily in Korean, focusing on business logic

### MyBatis Conventions
1. **Mapper Interface**: Java interface in `domain/` package
2. **Mapper XML**: Corresponding XML file in `src/main/resources/mappers/`
3. **Namespace**: Must match fully qualified interface name
4. **Result Mapping**: Use `resultType` or `resultMap`
5. **Parameter Binding**: Use `#{}` for prepared statements

Example:
```java
// Interface: domain/post/PostMapper.java
public interface PostMapper {
    int save(PostRequest params);
    PostResponse findById(Long id);
}
```

```xml
<!-- XML: resources/mappers/PostMapper.xml -->
<mapper namespace="com.study.domain.post.PostMapper">
    <insert id="save" parameterType="PostRequest">
        INSERT INTO tb_post (title, content, writer)
        VALUES (#{title}, #{content}, #{writer})
    </insert>
</mapper>
```

### Database Conventions
1. **Table Naming**: Prefix with `tb_` (e.g., `tb_post`)
2. **Column Naming**: Snake_case (e.g., `created_date`, `delete_yn`)
3. **Soft Delete**: Use `delete_yn` column (default: 0, deleted: 1)
4. **Boolean Flags**: Use `_yn` suffix (e.g., `notice_yn`)
5. **Timestamps**: `created_date`, `modified_date` (DATETIME)
6. **Primary Keys**: Typically `id` (BIGINT AUTO_INCREMENT)

### Pagination Pattern

The codebase implements a comprehensive pagination system with three key classes:

**1. SearchDto** (`common/dto/SearchDto.java`) - Request parameters
```java
SearchDto params = new SearchDto();
params.setPage(1);           // Current page (default: 1)
params.setRecordSize(10);    // Records per page (default: 10)
params.setPageSize(10);      // Page numbers to display (default: 10)
params.setKeyword("search"); // Optional search keyword
params.setSearchType("title"); // Optional search type
```

Key features:
- `getOffset()` method calculates SQL LIMIT offset: `(page - 1) * recordSize`
- Includes search functionality (keyword, searchType)
- Constructor sets sensible defaults

**2. Pagination** (`paging/Pagination.java`) - Calculation engine
```java
Pagination pagination = new Pagination(totalRecordCount, params);
```

Calculates:
- `totalPageCount` - Total number of pages
- `startPage` / `endPage` - Page range for UI (e.g., pages 1-10, 11-20)
- `limitStart` - SQL LIMIT offset
- `existPrevPage` / `existNextPage` - Navigation flags

**3. PagingResponse<T>** (`paging/PagingResponse.java`) - Response wrapper
```java
PagingResponse<PostResponse> response = new PagingResponse<>(posts, pagination);
return response;
```

Contains:
- `list` - List of results (generic type T)
- `pagination` - Pagination metadata

**Usage Pattern in Service Layer**:
```java
// 1. Get total count
int totalCount = postMapper.count(params);

// 2. Create pagination
Pagination pagination = new Pagination(totalCount, params);
params.setPagination(pagination);

// 3. Fetch paginated data
List<PostResponse> posts = postMapper.findAll(params);

// 4. Return wrapped response
return new PagingResponse<>(posts, pagination);
```

**MyBatis XML Query Example**:
```xml
<select id="findAll" parameterType="SearchDto" resultType="PostResponse">
    SELECT * FROM tb_post
    WHERE delete_yn = 0
    <if test="keyword != null and keyword != ''">
        AND title LIKE CONCAT('%', #{keyword}, '%')
    </if>
    ORDER BY id DESC
    LIMIT #{pagination.limitStart}, #{recordSize}
</select>
```

## Testing Guidelines

### Test Structure
- **Unit Tests**: For utility classes, services with mocked dependencies
- **Integration Tests**: For mappers, services with database access
- Use `@SpringBootTest` for integration tests
- Use `@DisplayName` and `@DisplayNameGeneration` for readable test names

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PostServiceTest

# Run with verbose output
./gradlew test --info
```

### Test Examples in Codebase
1. **PostMapperTest.java**: Demonstrates MyBatis mapper testing
   - Tests CRUD operations
   - Includes bulk insert for test data
   - Uses `@SpringBootTest` and `@Autowired`

2. **SortServiceTest.java**: Shows JUnit 5 features
   - `@ParameterizedTest` with `@ValueSource`
   - `assumeTrue` for conditional test execution
   - `@EnabledOnJre` for Java version-specific tests

### Test Data Management
- Tests may create/modify database records
- Consider using `@Transactional` on test classes for rollback
- PostMapperTest inserts 50 test posts

## Common Development Tasks

### Adding a New Domain/Feature
1. Create package under `domain/` (e.g., `domain/comment/`)
2. Create DTOs: `{Feature}Request`, `{Feature}Response`
3. Create service: `{Feature}Service`
4. Create mapper interface: `{Feature}Mapper`
5. Create mapper XML: `resources/mappers/{Feature}Mapper.xml`
6. Create controller: `{Feature}Controller` or `{Feature}RestController`
7. Add SQL table schema to database
8. Write tests: `{Feature}MapperTest`, `{Feature}ServiceTest`

### Modifying Post Functionality
- **Controller**: `domain/post/PostController.java` (MVC views)
- **REST API**: `domain/post/PostRestController.java`
- **Business Logic**: `domain/post/PostService.java`
- **SQL Queries**: `resources/mappers/PostMapper.xml`
- **Templates**: `resources/templates/post/*.html`

### Adding New SQL Queries
1. Add method signature to mapper interface
2. Add corresponding query in mapper XML with matching `id`
3. Test with mapper integration test
4. Call from service layer

### Modifying Configuration
- **Database**: `application.properties` (connection, credentials)
- **Security**: `security/WebSecurityConfigure.java`
- **WebSocket**: `config/WebSocketConfig.java`
- **Logging**: `logback-spring.xml`, `log4jdbc.log4j2.properties`

### Adding Dependencies
1. Add to `build.gradle` `dependencies` block
2. Run `./gradlew build --refresh-dependencies`
3. Restart application

## Git Workflow

### Current Branch
- **Development Branch**: `claude/claude-md-mi2u96pyrxvhr52o-01DemkT7QB26nJ25rRTSXcTB`
- **Main Branch**: `master`

### Commit Guidelines
1. Write clear, concise commit messages
2. Use English for commit messages (codebase uses Korean)
3. Prefix with action: "Add", "Fix", "Update", "Refactor"
4. Example: "Add pagination to post list", "Fix SQL injection in search"

### Pushing Changes
```bash
# Push to development branch
git push -u origin claude/claude-md-mi2u96pyrxvhr52o-01DemkT7QB26nJ25rRTSXcTB

# If network error, retry with exponential backoff
# The system will automatically retry up to 4 times
```

### Creating Pull Requests
1. Ensure all tests pass: `./gradlew test`
2. Build successfully: `./gradlew build`
3. Push changes to branch
4. Create PR to `master` branch
5. Include summary of changes and test plan

## Important Files & Their Purposes

| File | Purpose |
|------|---------|
| `BoardApplication.java` | Main Spring Boot application entry point, enables Eureka |
| `application.properties` | Database connection, MyBatis config, dev settings |
| `DatabaseConfig.java` | HikariCP pool, MyBatis SqlSessionFactory |
| `WebSecurityConfigure.java` | Spring Security, JWT configuration, CSRF settings |
| `PostMapper.xml` | All SQL queries for post operations |
| `PostController.java` | MVC endpoints for post CRUD (returns views) |
| `PostRestController.java` | REST API endpoints for posts (returns JSON) |
| `PostService.java` | Business logic for posts |
| `Pagination.java` | Pagination calculation (offset, limit, page ranges) |
| `ExcelController.java` | Excel file generation with Apache POI |
| `LoggerInterceptor.java` | Logs all HTTP requests/responses |
| `LogAll.java` | AOP aspect for method-level logging |

## Configuration Reference

### Database Configuration (application.properties)
```properties
spring.datasource.hikari.driver-class-name=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
spring.datasource.hikari.jdbc-url=jdbc:log4jdbc:mysql://localhost:3306/board?serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.hikari.username=root
spring.datasource.hikari.password=zen911!@
```

### MyBatis Configuration
```properties
mybatis.configuration.map-underscore-to-camel-case=true
```
This auto-converts `created_date` → `createdDate` in DTOs.

### Development Mode Settings
```properties
spring.devtools.livereload.enabled=true
spring.resources.static-locations=file:src/main/resources/static/
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=file:src/main/resources/templates/
```

### Eureka Configuration
```properties
spring.application.name=discoveryservice
eureka.client.register-witheureka=false
eureka.client.fetch-registry=false
```
Note: Eureka client registration is disabled despite server being enabled.

## Security Considerations

### Current Security Setup
- **Spring Security**: Enabled but minimally configured
- **CSRF**: Likely disabled for REST APIs
- **JWT**: Auth0 JWT library present (`Jwt.java`)
- **Password**: Database credentials in plain text (consider Jasypt encryption)

### Recommendations for AI Assistants
1. **Never commit sensitive data**: Passwords, API keys, tokens
2. **SQL Injection**: MyBatis `#{}` prevents injection (use it over `${}`)
3. **XSS**: Thymeleaf auto-escapes HTML by default
4. **Authentication**: JWT implementation exists but verify before modifying
5. **HTTPS**: Application uses HTTP (port 8080), consider HTTPS for production

## Troubleshooting

### Common Issues

#### Database Connection Errors
```
Solution: Verify MySQL is running on localhost:3306
Check credentials: root / zen911!@
Ensure database 'board' exists
```

#### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080
# Kill process
kill -9 <PID>
# Or change port in application.properties
```

#### Build Failures
```bash
# Clean build artifacts
./gradlew clean
# Rebuild
./gradlew build --refresh-dependencies
```

#### Tests Failing
```
Check database is running
Verify test data doesn't conflict with existing data
Review PostMapperTest for bulk insert expectations
```

#### Hot Reload Not Working
```
Verify spring-boot-devtools is in dependencies
Check IDE supports automatic compilation
Restart application if changes don't appear
```

## Resources & Documentation

### Spring Boot Documentation
- Spring Boot 2.7.x: https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/
- Spring Security: https://docs.spring.io/spring-security/reference/
- Spring WebSocket: https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket

### MyBatis Documentation
- MyBatis 3: https://mybatis.org/mybatis-3/
- MyBatis-Spring-Boot: https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/

### Libraries
- HikariCP: https://github.com/brettwooldridge/HikariCP
- Lombok: https://projectlombok.org/
- Apache POI: https://poi.apache.org/
- Auth0 JWT: https://github.com/auth0/java-jwt

## AI Assistant Guidelines

### When Working with This Codebase

1. **Language Awareness**:
   - Code comments are primarily in Korean
   - Database contains Korean text (UTF-8 encoding)
   - UI labels and messages in Korean
   - Respect existing language conventions

2. **Database Operations**:
   - Always use MyBatis mappers (not JPA entities)
   - Add SQL to mapper XML files, not inline in Java
   - Use `#{}` for parameter binding (prevents SQL injection)
   - Test with mapper integration tests

3. **Code Style**:
   - Use Lombok annotations extensively
   - Follow domain-driven package structure
   - Maintain layered architecture (Controller → Service → Mapper)
   - Keep DTOs separate from entities

4. **Testing**:
   - Write integration tests for mappers
   - Use JUnit 5 features (demonstrated in SortServiceTest)
   - Run tests before committing: `./gradlew test`

5. **Configuration Changes**:
   - Document any changes to `application.properties`
   - Be cautious with database credentials
   - Test configuration changes locally

6. **Adding Features**:
   - Follow existing domain structure (`domain/{feature}/`)
   - Create corresponding mapper XML
   - Add tests for new functionality
   - Update this CLAUDE.md if architecture changes

7. **Security**:
   - Never disable security features without explanation
   - Use parameterized queries (MyBatis `#{}`)
   - Validate user input
   - Consider XSS, SQL injection, CSRF

8. **Performance**:
   - HikariCP is already optimized
   - Use pagination for large result sets (see `SearchDto`)
   - Log4JDBC logs all SQL (disable in production)

### Quick Reference Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test
./gradlew test

# Clean
./gradlew clean

# Check dependencies
./gradlew dependencies

# Run specific test
./gradlew test --tests PostServiceTest

# Debug mode
./gradlew bootRun --debug-jvm
```

### Project Statistics
- **Java Files**: ~33 source files
- **Test Files**: 4 test classes
- **Templates**: 10+ Thymeleaf HTML files
- **Build Tool**: Gradle 7.5 with wrapper
- **Java Version**: 1.8 (source compatibility)
- **Spring Boot**: 2.7.2
- **Repository Size**: ~4.5MB

---

**Last Updated**: 2025-11-17
**Document Version**: 1.0
**Maintained by**: AI Assistant (Claude)

For questions or updates to this documentation, please review the actual codebase and update accordingly.
