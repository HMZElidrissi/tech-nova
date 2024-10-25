## TechNova

This application is a basic User Management System built using Spring Core without Spring Boot, Spring MVC, and Spring Data JPA. It allows users to perform CRUD operations such as creating, viewing, updating, and deleting users through a web interface.

## Table of Contents
- [Project Structure](#project-structure)
- [Dependency Injection (DI)](#dependency-injection-di)
- [Inversion of Control (IoC)](#inversion-of-control-ioc)
- [Spring Beans](#spring-beans)
- [Bean Scopes](#bean-scopes)
- [ApplicationContext](#applicationcontext)
- [Component Scanning and Stereotype Annotations](#component-scanning-and-stereotype-annotations)
- [Spring Data JPA](#spring-data-jpa)
- [Spring MVC](#spring-mvc)
- [Installation and Setup](#installation-and-setup)

### Project Structure

```
.
├── pom.xml
├── README.md
├── src
│ ├── main
│ │ ├── java
│ │ │ └── ma
│ │ │     └── hmzelidrissi
│ │ │         ├── controllers
│ │ │         │ └── UserController.java
│ │ │         ├── models
│ │ │         │ └── User.java
│ │ │         ├── repositories
│ │ │         │ └── UserRepository.java
│ │ │         └── services
│ │ │             ├── UserServiceImpl.java
│ │ │             └── UserService.java
│ │ ├── resources
│ │ │ ├── application.properties
│ │ │ ├── config.xml
│ │ │ └── spring-mvc-config.xml
│ │ └── webapp
│ │     └── WEB-INF
│ │         ├── templates
│ │         │ ├── error.html
│ │         │ └── users
│ │         │     ├── empty.html
│ │         │     ├── form.html
│ │         │     ├── list.html
│ │         │     └── loading.html
│ │         └── web.xml
│ └── test
│     └── java
└── target
```

### Dependency Injection (DI)

Dependency Injection is a design pattern that allows the removal of hard-coded dependencies and makes it possible to change them, whether at runtime or compile time. It is a way to achieve Inversion of Control (IoC) by moving the responsibility of creating objects to an external entity.

### Inversion of Control (IoC)

Inverse of Control is a design principle in which the control of objects or portions of a program is transferred to a container or framework. It promotes loose coupling between objects and allows the container to manage the lifecycle of objects.

### Spring Beans

Spring Beans are Java objects that are managed by the Spring IoC container. They are created, configured, and managed by the container and can be accessed through the application context. Beans are defined in the Spring configuration file and can be injected into other beans or components.

### Bean Scopes

Bean Scope defines the lifecycle of a bean and how it is created and managed by the Spring container. There are several bean scopes available in Spring, including Singleton, Prototype, Request, Session, and Global Session.

- Singleton: A single instance of the bean is created and shared across the application.
- Prototype: A new instance of the bean is created each time it is requested.
- Request: A single instance of the bean is created for each HTTP request.
- Session: A single instance of the bean is created for each HTTP session.
- Global Session: A single instance of the bean is created for each global HTTP session.

```xml
<bean id="userService" class="ma.hmzelidrissi.services.UserServiceImpl" scope="singleton">
    <property name="userRepository" ref="userRepository"/>
</bean>
```

### ApplicationContext

ApplicationContext is the central interface for providing configuration information to an application. It is responsible for managing beans, handling events, and providing resources to the application. The ApplicationContext interface extends the BeanFactory interface and provides additional functionality such as internationalization, message resolution, and event propagation.

```java
ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
UserService userService = context.getBean("userService", UserService.class);
```

### Component Scanning and Stereotype Annotations

Component Scanning is a feature of Spring that allows the container to automatically detect and register beans based on certain criteria. Stereotype Annotations are used to indicate the role or purpose of a class or method in the application. Spring provides several Stereotype Annotations such as @Component, @Service, @Repository, and @Controller.

```java
@Component
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### Spring Data JPA

Spring Data JPA is a part of the larger Spring Data family that provides support for JPA-based data access. It simplifies the development of data access layers by providing repositories and query methods that can be used to interact with the database. Spring Data JPA also supports pagination, sorting, and auditing out of the box.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    void deleteById(Long id);
}
```

### Spring MVC

Spring MVC is a web framework that is built on top of the Spring Core framework. It provides a model-view-controller architecture that can be used to develop web applications. Spring MVC supports the development of RESTful web services and provides features such as request mapping, data binding, and validation.

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/templates/"/>
    <property name="suffix" value=".html"/>
</bean>
```

### Installation and Setup

To run the application, you will need to have Java and Maven installed on your machine. You can build the project using the following command:

```bash
mvn clean install
```

You can then deploy the WAR file to a servlet container such as Tomcat or Jetty. The application will be accessible at `http://localhost:8080/tech-nova/`.
