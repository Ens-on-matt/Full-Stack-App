# Full Stack University DB Management Application

![image](https://github.com/user-attachments/assets/5085006d-b321-41fa-b0ef-9f57492b02a3)

This project contains a implementation of university database management application.

The purpose of this application was to create a full-stack application that consists of technologies and libraries that are used within the corporate world.


## Typical use
The example user would be an adminsitrator, who would interact with the React application to access and make changes to the database as appropriate. 

Users are able to access, create, update, save and delete staff, students, courses and degrees intuitively via the front-end.

![image](https://github.com/user-attachments/assets/2fa7074a-1b54-43af-83f6-bbf3ba1bf2fe)


A search option is included to let administrators easily find which user they want to edit.


This is useful to find a specific user amongst a large amount of entries. 

For example, if the student Nolan Roy wants to change bachelors.
To find Nolan Roy, we can search for his first name out of all 2000 students

![image](https://github.com/user-attachments/assets/9fbcf187-b672-4592-8318-0bf2a16032c8)


Click on the card for Nolan Roy and edit his degree from the dropdown


![image](https://github.com/user-attachments/assets/dbde6691-3dd3-417a-95f3-cd28af6f8366)

![image](https://github.com/user-attachments/assets/9120bb90-a52b-442f-ab12-6df55c8aaaaa)

And press update - you'll be notified via toastify whether it was successful or not.

![image](https://github.com/user-attachments/assets/9d9302fd-89d5-4224-8c47-e0dac9dc1417)


# REQUIRED DEPENDENCIES

- [Node.js](https://nodejs.org/en)
- [Maven](https://maven.apache.org/install.html)
- [PostgreSQL](https://www.postgresql.org/download/)


# INSTALLATION

First download this repository. 

Using a postgreSQL database manager of your choice, create a new empty database

Open sample-project-backend in IntelliJ (or your IDE of choice)
  - Edit the applications.properties to point the URL, username and password of spring.datasource to this newly made database
    - Will use port 8080 by default (server.port)
  - After this, run the project
  - Then maven will install any dependencies and run. Flyway will populate the database with initial test data

Open sample-project-frontend and run the following commands:
1. `npm install -i` to install all dependencies
2. Update the API_URL in src/api/UniService.tsx to the location of the Spring Boot Application (if required).
3. `npm run dev` to run the application

# TECHNOLOGIES:

The React application communicates with Spring Boot microservices to perform queries and updates to the data.

This implementation of a full-stack application utilises many sophisticated features, allowing for further scaling and development.

This application consists of 3 applications that communicate between each other:
1. React Frontend
2. Microservice Backend (Spring Boot)
3. PostgreSQL database

![image](https://github.com/user-attachments/assets/074c086b-e280-497e-aacc-4fb53835b306)


## Database data
The PostgreSQL database is filled with test data from 3 PostgreSQL scripts using Flyway. 

ALL DATA INCLUDED DOES NOT CONTAIN INFORMATION ON ANY REAL LIFE PEOPLE. 

ANY ASSOCIATIONS WITH ANY LIVING/DEAD PERSONS IS THE RESULT OF PURE COINCIDENCE.
