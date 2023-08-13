# 1. Requirements to run this project 

The requirements to run this project are the following:
  * Java JDK 17 
  * Maven 3.9.4 or higher

An IDE that can run a Java project is also recommended, but not obligatory

On a step-to-step on how install Java you can use the following link: https://www.geeksforgeeks.org/download-and-install-java-development-kit-jdk-on-windows-mac-and-linux/

On a step-to-step on how install Java you can use the following link: https://phoenixnap.com/kb/install-maven-windows

# 2. How to run the project

In order to run the project you can use an IDE or run it through the command line. For the sake of brevity I'll explain how to run it through the command line, which is a lot faster:

## How to run the project through the command line:

In order to run the project through the command line, simply unzip it, go inside the folder it was extracted to an open a terminal. Once it's open, you will only have to run the following command: mvn integration-test spring-boot:run

When you see this message (underlined with red), the app is running: 


<img width="1269" alt="AppRunning" src="https://github.com/miguelfferreira/ClassesBooker/assets/37029487/0398d8f9-4b0a-4a71-a346-a04c0f92d118">

To use the app just go to the following url: http://localhost:8080/swagger-ui/index.html

<img width="1280" alt="Swagger" src="https://github.com/miguelfferreira/ClassesBooker/assets/37029487/a40d7112-4749-4de1-9468-e6c8fec21150">

Here, you can test all the available endpoints asked for this technical task.

# 3. Database

I've used an in-memory database H2, simply because it's a lot closer to a real-world working environment. It allowed me to use JPA, as you will be able to check on the code.

It is also possible to access it its console via the following url: http://localhost:8080/h2-console

<img width="1277" alt="H2" src="https://github.com/miguelfferreira/ClassesBooker/assets/37029487/f15e017b-3fcc-47b8-bf6a-de77a8a079ff">

When you click on Connect, it will take you to its main console, where you can see the tables structure, execute queries and more:

<img width="1277" alt="H2" src="https://github.com/miguelfferreira/ClassesBooker/assets/37029487/2235aa13-707e-4120-9f52-b42da73aa4d7">

# 4. Noteworthy Points

Besides the barebones endpoints asked in the assignment, I've added the following:

## Validations 

There are a lot of validations that have been implemented since basic ones (check if given value is null), to more complex ones, mostly around the possibility of adding and deleting classes.

### /classes endpoint

* On the GET method, I've added pagination, and the filter parameters are, beyond the pagination ones, the name, the startDate and the endDate
* On the DELETE method, the number of classes to be deleted is based on the interval of days between the starDate and the endDate (date format must be dd-MM-yyyy). If endDate is not passed only the class with startDate is deleted (if it exists)
* I've also added a PUT method, which allows the edition of one specific Class

### /bookings endpoint

* On the DELETE method, it deletes the booking for the given member, on the given date

## Testing 

Tests have been implemented, at the controllers level.

## Documentation:
Besides the documentation available on the aforementioned url: http://localhost:8080/swagger-ui/index.html, the is also a more detail documentation on the on the following url: http://localhost:8080/v3/api-docs. It is in Json format:

<img width="1274" alt="API Documentation" src="https://github.com/miguelfferreira/ClassesBooker/assets/37029487/a2b32910-ea3d-4af6-8f0c-fcd235a4137c">








