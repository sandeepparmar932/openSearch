# Product Catalog with OpenSearch Dual-Write Architecture

A high-performance Spring Boot application demonstrating the integration of a relational database (Source of Truth) with OpenSearch (Search Engine) using a dual-write architecture.

This project provides blazing-fast, typo-tolerant full-text search capabilities over a product catalog while maintaining strict relational data integrity.

### Tech Stack
* **Framework:** Java 25, Spring Boot 4.x
* **Database:** Relational DB (via Spring Data JPA/Hibernate)
* **Search Engine:** OpenSearch 3.0.6
* **Infrastructure:** Docker & Docker Compose
* **Observability:** OpenSearch Dashboards

### Core Features
* **Synchronous Dual-Write:** Ensures data consistency by writing to the relational database first, then syncing the mapped document to OpenSearch.
* **Fuzzy Search:** Implements Levenshtein distance algorithms to handle user typos (e.g., searching "loptop" returns "laptop").
* **Advanced Querying:** Utilizes `NativeSearchQuery` and `multiMatchQuery` for complex, multi-field text analysis.
* **Separation of Concerns:** Maintains separate `Entity` classes for database normalization and flattened `Document` classes for search optimization.

### Local Setup

1. **Start the Infrastructure:**
   Ensure Docker is running, then spin up the OpenSearch cluster and Dashboards.
   `docker-compose up -d`

2. **Verify Containers:**
    * OpenSearch REST API: `http://localhost:9200`
    * OpenSearch Dashboards: `http://localhost:5601`

3. **Run the Application:**
   Start the Spring Boot application. It will automatically connect to port 9200 and create the necessary indices.

### API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/api/products` | Create a new product (writes to DB & OpenSearch) |
| GET | `/api/products/{id}` | Fetch exact product by ID (reads from DB) |
| PUT | `/api/products/{id}` | Update product details across both data stores |
| DELETE | `/api/products/{id}` | Delete product from both data stores |
| GET | `/api/products/search?keyword={text}` | Perform fuzzy full-text search (reads from OpenSearch) |


### Architecture Flow: Dual-Write & Advanced Search

sequenceDiagram
autonumber
actor Client
participant Controller as ProductController
participant Service as ProductService
participant DB as Relational Database (MySQL/H2)
participant OSRepo as OpenSearchRepository
participant SearchService as AdvancedSearchService
participant OS as OpenSearch Cluster

    Note over Client, OS: Flow 1: Synchronous Dual-Write (Data Ingestion)
    Client->>Controller: POST /api/products
    Controller->>Service: createProduct(ProductEntity)
    
    rect rgb(230, 245, 255)
        Note right of Service: 1. Save to Source of Truth
        Service->>DB: jpaRepository.save(ProductEntity)
        DB-->>Service: Saved ProductEntity (with auto-generated ID)
    end
    
    Service->>Service: Map ProductEntity to ProductDocument
    
    rect rgb(230, 255, 230)
        Note right of Service: 2. Sync to Search Index
        Service->>OSRepo: searchRepository.save(ProductDocument)
        OSRepo->>OS: Index Document (REST API Call)
        OS-->>OSRepo: 201 Created
        OSRepo-->>Service: Document Indexed Successfully
    end
    
    Service-->>Controller: Saved ProductEntity
    Controller-->>Client: 201 Created (JSON Response)

    Note over Client, OS: Flow 2: Advanced Fuzzy Search (Data Retrieval)
    Client->>Controller: GET /api/products/search?keyword=laptop
    Controller->>SearchService: advancedSearch("laptop")
    
    rect rgb(255, 245, 230)
        Note right of SearchService: 3. Query the Search Engine directly
        SearchService->>OS: multiMatchQuery (Fuzziness.AUTO)
        OS-->>SearchService: Return SearchHits (Matched Documents & Relevance Scores)
    end
    
    SearchService->>SearchService: Map SearchHits to List<ProductDocument>
    
    SearchService-->>Controller: List<ProductDocument>
    Controller-->>Client: 200 OK (JSON List)
### Future Enhancements
* Transition from synchronous dual-writes to an asynchronous event-driven sync using Apache Kafka or Debezium (CDC).
