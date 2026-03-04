# Library Management System

## Required links:
### [SonarCloud analysis](https://sonarcloud.io/project/overview?id=lKopeYka_LibraryManagement2)
### [ER-Diagram](docs/ER-Diagramm.png)

## Overview
This is a Java Spring Boot REST API for managing books in a library domain. The project is being developed as part of a series of laboratory works demonstrating modern backend development principles.

## Features
- CRUD for books, authors, readers, loans, and categories
- OneToMany relationships (Author → Books, Reader → Loans)
- ManyToMany relationship (Books ↔ Categories)
- Book filtering by author using @RequestParam
- Book lookup by ID using @PathVariable
- N+1 query problem demonstration and solution with @EntityGraph
- Transaction management showing partial save vs full rollback
- Layered architecture: Controller → Service → Repository
- DTOs and mapping between Entity and API response
- Checkstyle for code consistency
