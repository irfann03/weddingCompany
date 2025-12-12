# Tech stack
Java (JDK 17+ recommended)

Spring Boot (Spring Web, Spring Data MongoDB, lombok)

MongoDB Atlas (cloud MongoDB)

Maven as build tool

# Prerequisites
JDK 17 or higher installed

Maven installed

MongoDB Atlas account and cluster created

# MongoDB Atlas setup
Log in to MongoDB Atlas and create a cluster (free/shared is fine for development).

Create a database user with username and password.

Under Network Access, add your IP address (0.0.0.0/0 only for local dev/testing).


Go to your cluster → Connect → copy the connection String.

# Copy the connection string
mongodb+srv://<username>:<password>@<cluster-name>.mongodb.net/<dbName>?retryWrites=true&w=majority

Replace username, password, cluster-name, and dbName with your values.

# Application.properties

spring.application.name=WeddingCompanyBackend

server.port=8080

spring.data.mongodb.uri= your_connection_string

spring.data.mongodb.database=db_name

app.jwt.secret=your_secret_key

app.jwt.expiration-ms=3600000

logging.level.org.springframework.data.mongodb.core.MongoTemplate=INFO

logging.level.org.mongodb.driver=INFO

# Run the application

git clone https://github.com/irfann03/weddingCompany.git

cd weddingCompany

mvn clean install

mvn spring-boot:run

# Design Architecture

<img width="564" height="416" alt="Screenshot 2025-12-13 at 3 28 45 AM" src="https://github.com/user-attachments/assets/5fdc3246-44f7-4995-8abe-a92f2b62ed46" />
