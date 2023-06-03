<img src="assets/images/moneybag-logo.svg" alt="Moneybag logo" width=64 />

# Moneybag - Backend

[![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=flat&logo=java&logoColor=white&color=f1931c)](https://www.java.com/en/)
[![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=flat&logo=spring&logoColor=white)](https://spring.io)
[![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=flat&logo=Apache%20Maven&logoColor=white)](https://maven.apache.org)
[![MySql](https://img.shields.io/badge/MySQL-00000F?style=flat&logo=mysql&logoColor=white&color=3d6e93)](https://www.mysql.com)
[![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=flat&logo=swagger&logoColor=white)](https://swagger.io)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white)](https://www.postman.com)

## 📝 Description

Moneybag is a tool for **managing personal finances** that allows users to create and categorize records, analyze income
and expenses, and manage multiple accounts.

The backend was written in **Java** using the **Spring framework** and uses a MySql database for data persistence. The
frontend
is a single-page application connected via REST API and built with TypeScript, **Vue.js**, and Vuetify.

## 🔗 Links

- [📺 Video example](https://www.youtube.com/watch?v=rzZ-Xvy9iwY)
- [💅 Frontend repo](https://github.com/janbabak/Moneybag-FE)
- [📯 API documentation (Post man)](https://documenter.getpostman.com/view/131905572s93CRKWwv#b9ffcedf-337f-4546-8095-5740e9047e96)
- [📄 API documentation (Swagger)](https://janbabak.github.io/Moneybag-BE/)

## ⚽️ Project Goals

I created this project for several reasons. Firstly, I wanted to **experiment with various technologies**. Secondly, I
aimed
to experience all stages of the software development cycle. Additionally, I intended to add work to my portfolio that
would demonstrate my development skills.

## 🏗️ Realization

To begin, I made a list of the **necessary features** and selected the appropriate technologies, and then started
developing the backend server.

I developed the **backend** in **Java** and **Spring** framework and chose the MySql database for data persistence. The
application
utilizes the **MVC architecture**. The database runs in a **Docker** container. Authentication and authorization are
provided by
**JWT token** using **Spring Security**. The API generates [Open Api](https://janbabak.github.io/Moneybag-BE/)
documentation automatically. Additionally, I created
[documentation](https://documenter.getpostman.com/view/13190557/2s93CRKWwv#b9ffcedf-337f-4546-8095-5740e9047e96) for the
API using Postman, which includes example requests and responses.

## 🚀 Features

- Multiple financial accounts
- Add records and categorize them
- Analytic of categories, incomes, expenses, cash flow...
- Charts
- Responsive web interface

## 🧑‍🔬 Technologies

- [Java](https://www.java.com/en/)
- [Spring](https://spring.io)
- [Maven](https://maven.apache.org)
- [Docker](https://www.docker.com)
- [MySql](https://www.mysql.com)
- [git](https://git-scm.com)

## ❌ Issues

During the development, I encountered some issues. One of them was adding filtering and sorting parameters to the "get
all records" endpoint. To solve this problem, I used the specification-arg-resolver library which can map request
parameters to Jpa specifications and extended the record repository by PagingAndSortingRepository, and
JpaSpecificationExecutor.

## 😁 Conclusion

I originally planned to create a much smaller project but finished with a larger one. I **gained new knowledge** during
development - for instance, how to elegantly add **filtering parameters** to the API endpoint or use **JPQL** for more
complex database queries. So, I am satisfied with the result of my work.

## ✅ Software requirements

- Java 17
- Docker, Docker compose

## 🎬 How to run

### Clone repository

```bash
git clone https://github.com/babakjan/Moneybag-BE.git
cd Moneybag-BE/
```

### Start database

```bash
docker compose up -d
```

### Start the app

- Create database schema - open `./src/main/resources/application.properties` find the following lines and (un)comment
  them
  to look like this:
    ```
    #spring.jpa.hibernate.ddl-auto=none
    spring.jpa.hibernate.ddl-auto=create
    ```
- Run the app (app will run on http://localhost:8000/api)
    ```bash
    ./mvnw spring-boot:run
    ```
- If you want to initiate the database with test data, stop the app and undo the changes you've done in the application
  properties file,
  plus make sure, that the following line is uncommented
  ```
  spring.sql.init.mode=always
  ```
- run the app again
    ```bash
    ./mvnw spring-boot:run
    ```
- If you want to run the app repeatedly, comment the previously uncommented line because you don't want to insert test
  data
  every time you restart the app.

- Swagger documentation of running app can be found at http://localhost:8000/api/swagger-ui/index.html

## 🎆 Screenshots

<img src="assets/images/landing-shadow,rounded.webp" alt="Dashboard" />
<img src="assets/images/dashboard-shadow,rounded.webp" alt="Dashboard" />
<img src="assets/images/records-shadow,rounded.webp" alt="Dashboard" />
<img src="assets/images/analytic-shadow,rounded.webp" alt="Dashboard" />
