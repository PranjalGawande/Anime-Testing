
# Mutation Testing on Anime Review System

This repository contains the source code and test files for the **Anime Review System**, which includes unit tests, integration tests. These tests ensure the reliability, accuracy, and performance of the system by validating the behavior of individual components as well as the integration between different modules.

---

## About the Project

The **Anime Review System** enables users to browse, review, and manage anime data. The project is built on a robust backend with comprehensive test coverage to ensure high-quality code.

---

## Technologies Used

- **Java**: The programming language used.
- **Spring Boot**: Framework for building the backend.
- **JUnit**: For writing and running test cases.
- **PIT Mutation Testing**: To ensure the effectiveness of test cases.


---

## Commands to Run Tests

1. **To compile the code and run all test cases**:
   ```bash
   mvn clean install
   ```

2. **To generate mutation test reports**:
   ```bash
   mvn clean test pitest:mutationCoverage
   ```

   - The PIT mutation test reports will be generated in the `target/pit-reports` folder.

---

## Mutation Testing

Mutation testing is used to verify the effectiveness of the test suite by introducing small changes (mutants) to the code and ensuring the tests detect these changes. This helps improve the quality of the test cases.

---

## Contributions

The test files were written and organized collaboratively by our team members as follows:

### **Chittaranjan Chandwani**
- **Controller Tests**:
  - `UserControllerTest`
- **DTO Tests**:
  - `TokenTest`
- **Integration Tests**:
  - `UserServiceIntegrationTest`
- **Entity Tests**:
  - `UserTest`
  - `PermissionTest`
  - `RoleTest`
- **Service Tests**:
  - `JwtAuthFilterTest`
  - `JwtServiceTest`
  - `UserServiceUnitTest`
  - `AnimeApplicationTests`

### **Pranjal Gawande**
- **Controller Tests**:
  - `AnimeControllerTest`
- **DTO Tests**:
  - `ExtraDTOTest`
  - `ReviewDTOTest`
- **Integration Tests**:
  - `ReviewServiceIntegrationTest`
- **Entity Tests**:
  - `ReviewTest`
  - `WatchListTest`
- **Service Tests**:
  - `AnimeServiceTest`
  - `ReviewServiceUnitTest`
  - `WatchListServiceUnitTest`
  
---

## Authors

- **Chittaranjan Chandwani**
- **Pranjal Gawande**

--- 
