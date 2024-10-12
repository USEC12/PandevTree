# Telegram Bot for Managing Category Tree

## 📖 Description

This project is a **Telegram Bot** that allows users to **create**, **view**, and **delete** a hierarchical tree of categories. It provides an intuitive interface for managing categories directly from Telegram, making it easy to organize and manipulate data in a structured format.

---

## 🚀 Features

- **View Category Tree**: Display the entire category tree in a structured format.
- **Add Categories**:
  - Add root categories.
  - Add child categories to existing categories.
- **Remove Categories**: Delete a category and all its subcategories.
- **Help Command**: Get a list of all available commands and their descriptions.
- **Download Category Tree**: Export the category tree to an Excel file.
- **Upload Category Tree**: Import categories from an Excel file.

---

## 🛠️ Built With

- **Java 11**
- **Spring Boot**
- **PostgreSQL**
- **Hibernate (JPA)**
- **TelegramBots API**
- **Apache POI** (for Excel operations)
- **Maven**

---

## 📋 Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java JDK 11** or higher installed.
- **Maven** installed.
- **PostgreSQL** installed and running.
- A **Telegram Bot Token** from [BotFather](https://t.me/BotFather).

---


## 💡 Usage

Interact with your bot on Telegram using the following commands:

### Available Commands

- **`/help`**

  - **Description**: Displays a list of all available commands and their descriptions.
  - **Usage**: `/help`

- **`/viewTree`**

  - **Description**: Shows the entire category tree in a structured format.
  - **Usage**: `/viewTree`

- **`/addElement <category_name>`**

  - **Description**: Adds a new root category.
  - **Usage**: `/addElement Electronics`

- **`/addElement <parent_category> <category_name>`**

  - **Description**: Adds a new child category under an existing category.
  - **Usage**: `/addElement Electronics Mobile Phones`

- **`/removeElement <category_name>`**

  - **Description**: Removes a category and all its subcategories.
  - **Usage**: `/removeElement Mobile Phones`

- **`/download`**

  - **Description**: Downloads the category tree as an Excel file.
  - **Usage**: `/download`

- **`/upload`**

  - **Description**: Uploads categories from an Excel file.
  - **Usage**:
    1. Send `/upload` command.
    2. Attach the Excel file in the next message.

---



## 📂 Project Structure

- **`src/main/java`**: Contains the Java source code.
  - **`bot`**: Telegram bot implementation.
    - **`command`**: Command classes implementing bot functionality.
  - **`model`**: Entity classes representing database tables.
  - **`repository`**: Repository interfaces for data access.
  - **`service`**: Service classes containing business logic.
- **`src/main/resources`**: Contains application configuration files.
- **`pom.xml`**: Maven configuration file.

---
🤔 Challenges Faced
The following challenges were encountered while developing this project:

LazyInitializationException when working with initialization of child categories:

Problem: When displaying the category tree in the /viewTree command, a LazyInitializationException error was thrown because the child category collections were loaded lazily and outside of an active Hibernate session.
Solution: I used collection initialization via the Hibernate.initialize() method before leaving the session, and also considered alternatives such as fetch-join using a JPA query or using entity graphs.
Handling Excel file uploads and downloads:

Problem: The need to import and export categories from and to Excel format required the use of the Apache POI library. Due to the lack of experience with this library, difficulties with parsing and building the document arose.
Solution: I used Apache POI to create and read Excel files, adding manual row and cell processing to correctly handle the hierarchical structure of categories.
Errors when working with Telegram API:

Problem: When interacting with Telegram API, errors occurred when uploading and downloading files. The method of uploading files via the TelegramBots API was difficult due to the lack of documentation on working with large files.
Solution: I replaced the attempt to override the downloadFile() method with the use of the ready-made DefaultAbsSender method for downloading files, which eliminated the problem with overriding the final methods.
Testing integration with the PostgreSQL database:

Problem: It was necessary to correctly configure the connection between the bot and the PostgreSQL database, as well as provide migrations and automatic initialization of the database for tests.
Solution: I used Spring Data JPA with the correct configuration of database migrations and implemented functional tests to check the correct operation of the bot commands.
These challenges helped me gain a deeper understanding of how to work with the Telegram API, Hibernate, and interacting with the database via Spring Data JPA, which improved our understanding of the application architecture and improved the quality of the code.




