# Credit Application System

This is a Spring Boot 3 project with Kotlin that implements a credit system. The system includes operations such as user registration, update, search, and removal, as well as the creation and search of credits associated with these users. 
This project was developed during a Bootcamp at Digital Innovation One (DIO).

**Swagger for documentation (available at** http://localhost:8080/swagger-ui/index.html **) when the API is runnig**

## Features

1. **Credit Creation:** Create new credits in the database, providing details such as customer ID, credit value, and number of installments.
  
2. **Credit Search:** Search for credits based on customer ID or credit code.

3. **User Management:** Implement user registration, update, search, and removal functionalities.

4. **Validation:** Utilize Spring Validation for input validation.

 Tools 

- **Java 17 or higher** 

- **Spring Boot 3**

- **Kotlin** 

- **JUnit 5 and MockK for testing**

- **Spring Validation** 

- **Swagger for documentation**

## Running the API

To run the API using Gradle, navigate to the project root and use the following command: `gradle bootRun`

## Testing

Execute unit and integration tests using: `gradle test`
