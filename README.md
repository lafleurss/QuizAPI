Quiz API
=============================
## Overview

Implemented a simple RESTful API that simulates basic backend functionality for a quiz application.

### Summary of the API

This Quiz API contains 3 entities: `Quiz`, `Question`, and `Answer`. Each have an auto-generated numerical primary key. The `Quiz` entity has a name and maintains a collection of `Question` entities that are part of the the `Quiz` using an `@OneToMany` annotation, provided by JPA, to denote the relationship between the two tables in the database. The `Question` entity has a text field, the `Quiz` that it belongs to annotated with `@ManyToOne` representing the opposite side of the relationship stored in the `Quiz` class, and a collection of `Answer` objects annotated with `@OneToMany`. Finally, the `Answer` entity also has a text field, a boolean to denote if the given `Answer` is the correct one for the `Question` it belongs to, and the `Question` object that it belongs to annotated with `@ManyToOne`.

An entity relationship diagram is provided below that represents the database used by the Quiz API:
![quiz-api](https://user-images.githubusercontent.com/32781877/158852533-29305164-9e9e-41b2-a808-fb1d717b70cf.png)

---

### Endpoint Documentation

Created the following endpoints and implemented their functionality in the Controller, Service, and Repositories. 

- [ ] `GET quiz`
    - Returns the collection of `Quiz` elements

- [ ] `POST quiz`
    Creates a quiz and adds to collection
    - Returns the `Quiz` that it created

- [ ] `DELETE quiz/{id}`
    Deletes the specified quiz from collection
    - Returns the deleted `Quiz`

- [ ] `PATCH quiz/{id}/rename/{newName}`
    Rename the specified quiz using the new name given
    - Returns the renamed `Quiz`

- [ ] `GET quiz/{id}/random`
    - Returns a random `Question` from the specified quiz

- [ ] `PATCH quiz/{id}/add`
    Adds a question to the specified quiz
    - Receives a `Question`
    - Returns the modified `Quiz`
    
- [ ] `DELETE quiz/{id}/delete/{questionID}`
    Deletes the specified question from the specified quiz
    - Returns the deleted `Question`
