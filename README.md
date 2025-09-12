## Employees REST API

[![CI](https://github.com/amblessed/employees/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/Amblessed/employees/blob/master/.github/workflows/ci.yml)

[![Allure Report](https://img.shields.io/badge/Allure-Report-ED5C5C?logo=allure&logoColor=white)](https://amblessed.github.io/employees/)


This Employees REST API project— demostrates how to seamlessly integrate Java and Python to manage employee data. This project showcases how to combine the power of Spring Boot with the flexibility of Pytest, all backed by PostgreSQL.

# 🚀 Project Overview

This application provides a RESTful API for managing employee records, including CRUD operations (Create, Read, Update, Delete). It leverages:

- **Backend**: Java, Spring Boot with Spring Data JPA and Spring Security  
- **Database**: PostgreSQL  
- **Testing**: Python, Pytest for comprehensive REST API endpoint testing
- **Testcase**: JSON format
- **TestData**: DataFaker for generating realistic test data
- **CI/CD**: GitHub Actions
- **Reporting**: Allure Report
- **Build Tool**: Maven
- **Deployment**: GitHub Pages

**_By integrating Java and Python, this project demonstrates a flexible and scalable approach to backend development and testing._**

## Features

- **Employee Management**
    - Add, update, delete, and list employees
    - Track employee information: name, email, phone, department, position, salary, hire date, skills, and performance reviews
    - Active/inactive employee status

- **Department & Position Management**
    - Multiple departments (Engineering, Product, Analytics, HR, Finance, Sales, IT Support, Operations)
    - Positions and corresponding skills for realistic employee data

- **Automated Data Seeding**
    - Generates thousands of employees for development/testing environments
    - Unique emails and phone numbers
    - Realistic salaries (whole numbers), hire dates, and timestamps
    - Performance reviews based on department strengths and improvements

- **Database Support**
    - PostgreSQL for testing

- **Security & Roles**
    - Role-based access control (Employee, Manager, Admin)
    - Users table seeded with predefined credentials for development/testing

- **Spring Boot Actuator**
    - Health check endpoint: `/actuator/health`
    - Application info endpoint: `/actuator/info`


# 📁 Project Structure

The project is organized as follows:
```
employees/  
├── src/  
│   ├── main/  
│   │   ├── java/  
│   │   │   └── com/  
│   │   │       └── employees/ 
|   |   |           ├── config/ 
│   │   │           ├── controller/  
│   │   │           ├── entity/  
|   |   |           ├── exception/  
│   │   │           ├── repository/  
│   │   │           ├── security/  
│   │   │           └── service/  
│   │   └── resources/  
│   │       └── application.properties  
│   └── test/  
│       └── python/  
|           ├── conftest.py  
|           ├── db_connection.py  
|           ├── requirements.txt  
|           ├── test_config.py  
│           └── test_endpoints.py  
|           └── testcases.json  
├── .gitignore  
├── pom.xml  
└── pytest.ini  
```

# 🧪 Testing with Pytest

The project includes comprehensive tests for the REST API endpoints, ensuring functionality and reliability. These tests are located in the test_endpoints.py file and cover:

- Creating new employees  
- Retrieving employee details  
- Updating employee information  
- Deleting employee records

Pytest's simplicity and powerful features make it an excellent choice for testing in a Java-Python integrated environment.

# 🔐 Security Features
Spring Security is configured to secure the API endpoints. Access control is implemented to ensure that only authorized users can perform certain operations, such as creating or deleting employee records.

# 📈 Real-World Use Case
This project serves as a practical example of integrating Java and Python in a single workflow. The backend, built with Spring Boot and PostgreSQL, provides a robust and scalable solution for managing employee data. The use of Pytest for testing demonstrates how Python can complement Java in the development process, offering flexibility and efficiency.

# Future Work

- Batch inserts for performance optimization.
- Optional Docker setup for local development with PostgreSQL.
- Swagger/OpenAPI integration for API documentation.
- Logging with SLF4J instead of System.out.println.


# 💡 Conclusion
The Employees REST API project exemplifies how combining Java and Python can lead to a powerful and flexible development workflow. By leveraging Spring Boot, PostgreSQL, and Pytest, this project provides a comprehensive solution for managing employee data, from backend development to testing.

# 📄 License
This project is licensed under the MIT License.
