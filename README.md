# Quoted
A quote management application.
## Description
This is a simple quote management app. It allows you to create, edit, delete and view quotes and their respective sources and persist them in a database. You can also review random quotes and search quotes and sources by keyword.

## Features
- Create, edit, delete and view quotes
- Review random quotes
- Create, edit, delete and view sources (Books or Websites)
- Search quotes and by keyword or source, search sources by keyword

## User Manual
Can be found [here](UserManual.md).

## Requirements
- Java 19
- JavaFX 19
- Gradle 7.6 to build the project including dependencies
- Postgresql 14

## Dependencies
- [SLF4J](http://www.slf4j.org) for logging (using [Log4j](https://logging.apache.org/log4j/))
- [Project Lombok](https://projectlombok.org) for automatic generation of getters, setters, constructors, etc.
- [Hibernate](https://hibernate.org) for object-relational mapping
- [Jakarta Persistence](https://jakarta.ee/specifications/persistence) for Java Persistence API
- [Postgresql JDBC Driver](https://jdbc.postgresql.org) for database connection
- [JUnit 5](https://junit.org/junit5) for unit testing
- [Mockito](https://site.mockito.org) for mocking objects in unit tests

## Setup
1. Setup a local postgresql server. On MacOS, you can e.g. do that using [Homebrew](https://brew.sh) with the commands `brew install postgresql` and then `brew services start postgresql@14`. (Default user will be your local user name without a password.) See [here](https://www.postgresql.org/download/) for examples on how to do that on other platforms.
2. Remove the suffix `.example` from `src/main/resources/META-INF/persistence.xml.example` and replace `USER` and `PASSWORD` accordingly.
3. In case you want to use a different database, you can change the `hibernate.connection.url` and `hibernate.dialect` in `src/main/resources/META-INF/persistence.xml` accordingly. The program has only been tested with postgresql.
4. Create the database `quote_db` either using the `CREATE DATABASE quote_db;` command or by using the [pgAdmin](https://www.pgadmin.org) GUI.
5. In case you want to use a different database name or port, you must change the `hibernate.connection.url` in `src/main/resources/META-INF/persistence.xml` and the `DB_NAME` in `src/main/java/org/nico/quoted/config/BackendConfig.java` accordingly.
6. In case you want to run tests, also create a database `quote_db_test` or change the `DB_NAME_TEST` in `src/main/java/org/nico/quoted/config/BackendConfig.java` accordingly.
7. To run the application, run `./gradlew run` in the project root directory.
8. (To run tests, run `./gradlew test` in the project root directory.)
9. (To build a jar file, uncomment the jar section in [build.gradle](build.gradle) run `./gradlew jar` in the project root directory. The executable 'fat' jar file will be located in `build/libs`. To run the jar file, run `java -jar build/libs/quoted-1.0-SNAPSHOT.jar` in the project root directory. To run 'normally' again, comment out the jar section in [build.gradle](build.gradle) and go to step 7.)