# Full Stack University DB Management Application

This project contains a implementation of university database management application.

The purpose of this application was to create a full-stack application that consists of technologies and libraries that are used within the corporate world.


## Typical use
The example user would be a database manager, who would interact with the React application to access and make changes to the database as appropriate. 

Users are able to access, create, update, save and delete staff, students, courses and degrees of this mock database intuitively on the front-end.


SHOW IMAGE OF ADDING COURSE


Search functionality is included to let administrators easily find which user they want to edit.

For example, consider the following:


SHOW STUDENT PAGE


There are >140 pages of students. Using the search functionality I can find a specific user. 

For example, if the student Nolan Roy wants to change bachelors:


SHOW STUDENT PAGE


To find Nolan Roy, we can search for his first name


SHOW STUDENT PAGE AT/AFTER SEARCHING


Click on the card for Nolan Roy and edit his degree from the dropdown


SHOW NOLAN ROY PAGE WITH DEGREE DROPDOWN


And press update - you'll be notified via toastify whether it was successful or not.



# INSTALLATION
To install, first git clone this directory.


Open sample-project-backend in IntelliJ (or your IDE of choice)
  - If using IntelliJ, it should recognise the project and you can run it
  - If not, run main in SampleProjectApplication
  - Then maven should install any dependencies and it should run


Open sample-project-frontend and run the following commands:
1. `npm install -i`
2. `npm run dev`



# TECHNOLOGIES:

The React application communicates with Spring Boot microservices to perform queries and updates to the data.

This implementation of a full-stack application utilises many sophisticated features, allowing for further scale and development.

This application consists of 3 applications that communicate between each other:
1. React Frontend
2. Microservice Backend (Spring Boot)
3. PostgreSQL database'

The dataflow between applications occur as following:

Frontend <-> RESTful requests/responses <-> Spring Microservices <-> SQL queries <-> PostgreSQL database


## Database data
The PostgreSQL database is filled with mock data from 3 PostgreSQL scripts using Flyway. 

ALL DATA INCLUDED IS FAKE AND DOES NOT CONTAIN INFORMATION ON ANY REAL LIFE PEOPLE. 

ANY ASSOCIATIONS WITH ANY LIVING/DEAD PERSONS IS THE RESULT OF PURE COINCIDENCE.
