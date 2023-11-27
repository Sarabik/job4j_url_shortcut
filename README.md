# URL_SHORTCUT SERVICE

RESTful web service helps shorten url links and to ensure the safety of site users, who will use them (links will be replaced with links to this service).
The service works via REST API.

---

### General features of the service:

* Registration and authorization of users to obtain shortened links and view the total number of calls to registered url addresses.
* Transition to the original address by a shortened link without the need for authorization in the service

---

### Technology stack

* Java 17
* Spring Boot 2.6.15
* Spring Data JPA 2.6.15
* Spring Web 5.3.27
* Spring Security 5.3.27
* Spring Test 5.3.27
* Java JWT 3.4.0
* Lombok 1.18.26
* PostgreSQL 42.3.8
* H2 Database 2.2.220
* Liquibase 4.5.0
* Maven 3.6.2

---

### Run the project

1) To run the project, you need to clone the project from this repository;
2) Then you need to create a local database "url_shortcut";
3) Specify the login and password for the database you created in the db/liquibase.properties file;
4) Run liquibase to pre-create tables;
5) Launch the application using one of the following methods:
   * Through the Main class, located in the folder src\main\java\ru\job4j\Job4jUrlShortcutApplication;
   * Compiling and running the project via maven with mvn spring-boot:run;
   * After building the project via maven and running the built file with the command java -jar job4j_url_shortcut-0.0.1-SNAPSHOT.jar;

---

### Interaction with the application

1) Site registration (performed without authorization)
   http://localhost:8080/registration
   POST request body example: {"site": "google.com"}
   Response body example: {"registration": "true", "login": "4rgr5HeR", "password": "fG2Pfa34fds"}
2) Authorization (performed without authorization)
   http://localhost:8080/login
   POST request body example: {"login": "4rgr5HeR", "password": "fG2Pfa34fds"}
   Response header example: "Authorization": "Bearer gsW2gl75KW..."
3) URL registration (request header with authorization token)
   http://localhost:8080/convert
   POST request body example: {"url": "site.com/page1"}
   Response body example: {"shortcut": "H2b0sqd"}
4) Redirecting (performed without authorization)
   http://localhost:8080/redirect/H2b0sqd
   GET request body example: {"shortcut": "H2b0sqd"}
   Response: redirect to associated link
5) Getting statistics (request header with authorization token)
   http://localhost:8080/statistic
   GET request
   Response example: {"url": "site.com/page1", "total": "21"}
