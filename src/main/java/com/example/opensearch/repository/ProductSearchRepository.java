package com.example.opensearch.repository;

import com.example.opensearch.model.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

   /* // You can still use basic derived queries
    List<ProductDocument> findByNameContainingIgnoreCase(String name);

    // But now you can also do OpenSearch-specific JSON queries if needed
    @Query("{\"match\": {\"description\": {\"query\": \"?0\"}}}")
    List<ProductDocument> findByDescriptionMatch(String keyword);*/
}
