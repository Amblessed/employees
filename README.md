## Employees REST API

[![CI](https://github.com/amblessed/employees/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/Amblessed/employees/blob/master/.github/workflows/ci.yml)

[![Allure Report](https://img.shields.io/badge/Allure-Report-ED5C5C?logo=allure&logoColor=white)](https://amblessed.github.io/employees/)


The Employees REST API demonstrates how to integrate **Java (Spring Boot + Spring Data JPA)** for backend development with **Python (Pytest)** for test automation. It showcases a complete workflow for managing employee data, backed by **PostgreSQL**, with automated tests and CI/CD integration to ensure reliability.

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
├───src
│   ├───main
│   │   ├───java
│   │   │   └───com
│   │   │       └───amblessed
│   │   │           └───employees
│   │   │               ├───config
│   │   │               ├───controller
│   │   │               ├───entity
│   │   │               ├───exception
│   │   │               ├───mapper
│   │   │               ├───repository
│   │   │               ├───security
│   │   │               └───service
│   │   └───resources
│   │       ├───db
│   │       ├───static
│   │       └───templates
│   └───test
│       ├───java
│       │   └───com
│       │       └───amblessed
│       │           └───employees
│       │               └───repository
│       ├───python
│       │   └───__pycache__
│       └───resources
│           └───testcases
```

### 🔧 EmployeeSeeder: Dynamic Test Data Generator

The `EmployeeSeeder` class populates the database with 100 randomized employee records, each linked to a unique user and role. It supports:

- Role-based access testing (`ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_ADMIN`)
- Password encoding for Spring Security
- Plaintext password export for Pytest API tests
- Unique email, phone, and user ID generation
- Batch persistence for performance
- JSON output to `src/test/resources/user_details.json`

This enables seamless integration between backend authentication and external test automation frameworks.


# 🧪 Testing with Pytest

Comprehensive tests are included to validate the REST API endpoints and ensure consistent functionality.  
The test cases are located in the `resources/testcases` folder and cover:

- Creating new employees
- Retrieving employee details
- Updating employee information
- Deleting employee records

Pytest's simplicity and powerful features make it an excellent choice for testing in a Java-Python integrated environment.

# 🔐 Security & API Access

The Employees REST API is secured using **Spring Security** with **Basic Authentication**. Access to endpoints is role-based:

- `ADMIN` and `MANAGER` can retrieve all employees.
- `ADMIN` and `MANAGER` can retrieve any employee by ID.
- Individual employees can access only their own record.

Access control is implemented using `@PreAuthorize` annotations on the endpoints. Fine-grained control, like allowing employees to see only their own data, is achieved through custom security checks.

---

### Example Requests with Basic Auth (Python)

#### Get All Employees (Admin/Manager Only)
```python
import requests

url = "http://localhost:8080/api/employees"
response = requests.get(url, auth=("manager_username", "password"))

print(response.status_code)
print(response.json())
```


# 📈 Real-World Use Case
This project serves as a practical example of **integrating Java and Python in a single workflow**. The backend, built with Spring Boot and PostgreSQL, provides a robust and scalable solution for managing employee data. The use of Pytest for testing demonstrates how Python can complement Java in the development process, offering flexibility and efficiency.

## Roadmap / Next Steps

This project is under active development. Current focus areas:

- ✅ Core CRUD API with Spring Boot and Spring Data JPA
- ✅ Basic test automation using Python + Pytest
- ✅ CI workflow for building and running tests

Planned improvements:
- 🔒 Add authentication/authorization (JWT)
- 📝 Improve API documentation with OpenAPI/Swagger
- 📊 Expand Pytest suite with more negative and edge-case scenarios
- 🐳 Containerize API + database (Docker) for easier deployment and testing
- 🚀 Enhance CI/CD pipeline (test reports, coverage badges, automated builds)

# 💡 Conclusion
The Employees REST API project demonstrates how Java and Python can be used together to support a reliable development workflow. Using Spring Boot, PostgreSQL, and Pytest, the system provides a complete approach to managing employee data, covering both backend implementation and testing.

# 📄 License
This project is licensed under the MIT License.
