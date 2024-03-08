# Web Scraping Academia

## Introduction
This project aims to facilitate access to information from the Springer academic website using web scraping. It utilizes MongoDB database with Elasticsearch query structures and is implemented using the Java Spring framework, MongoDB repository, and Jsoup for web scraping. Additionally, MongoDB Compass and NoSQL queries are employed for database management.

## Project Overview
The project consists of three main components:

1. **Web Scraping:**
    - Retrieves information from the [Springer](https://link.springer.com/) academic website based on user-entered keywords.
    - Displays details of at least the top 10 academic publications on a custom-built web page.
    - Utilizes HTML parsing or request methods to access the desired data from the Springer website.
    - Downloads PDF files for each publication.

2. **Database:**
    - Stores the scraped data using MongoDB.
    - Required publication information includes:
        - Publication ID
        - Publication title
        - Author names
        - Publication type (research paper, review, conference, book, etc.)
        - Publication date
        - Publisher name
        - Keywords (searched on the academic search engine)
        - Keywords (related to the article)
        - Abstract
        - References
        - Citation count
        - DOI number (if available)
        - URL address
    - MongoDB Compass and NoSQL queries are used for database management.

3. **Web Interface:**
    - Creates a web page to display the retrieved publication information.
    - Provides a text area for users to enter keywords for searching publications.
    - Initially displays all records from the database upon page load.
    - Enables dynamic searching with automatic spelling correction suggestions.
    - Includes dynamic filtering options based on various attributes of publications.
    - Allows sorting by publication date and citation count.

## Usage
1. **Installation:**
    - Clone the repository:
        ```
        git clone https://github.com/your/repository.git
        ```
    - Navigate to the project directory:
        ```
        cd project-directory
        ```
    - Install dependencies:
        ```
        // Add commands to install any dependencies if needed
        ```

2. **Running the Application:**
    - Start the application:
        ```
        // Add commands to start the application
        ```

3. **Accessing the Web Interface:**
    - Once the application is running, access the web interface by navigating to [http://localhost:port](http://localhost:port) in your web browser.

4. **Using the Web Interface:**
    - Enter keywords in the provided text area to search for publications.
    - Browse through the displayed publications and click on a publication title for detailed information.
    - Use the dynamic filtering options to refine the displayed publications.
    - Sort the publications by publication date or citation count.

## MongoDB Compass and NoSQL Queries
Include instructions or examples of using MongoDB Compass and NoSQL queries for managing the database.

## Contributors
- [Emre Terzi](https://github.com/emretterzi)




## Acknowledgments
Mention any libraries, articles, or resources used in the project.


    

