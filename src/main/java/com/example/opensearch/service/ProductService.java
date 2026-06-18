package com.example.opensearch.service;

import com.example.opensearch.model.ProductDocument;
import com.example.opensearch.model.ProductEntity;
import com.example.opensearch.repository.ProductRepository;
import com.example.opensearch.repository.ProductSearchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository jpaRepository;
    private ProductSearchRepository searchRepository;


    @Transactional
    public ProductEntity createProduct(ProductEntity productEntity) {
        ProductEntity savedEntity = jpaRepository.save(productEntity);
        syncToOpenSearch(savedEntity);
        return savedEntity;
    }

    @Transactional
    public ProductEntity updateProduct(Long id, ProductEntity updatedEntity) {
        ProductEntity existingEntity = jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update fields
        existingEntity.setName(updatedEntity.getName());
        existingEntity.setSku(updatedEntity.getSku());
        existingEntity.setDescription(updatedEntity.getDescription());
        existingEntity.setPrice(updatedEntity.getPrice());

        // 1. Save updated entity to DB
        ProductEntity savedEntity = jpaRepository.save(existingEntity);

        // 2. Sync the updated data to OpenSearch
        syncToOpenSearch(savedEntity);

        return savedEntity;
    }

    public ProductEntity getProductById(Long id) {
        // Read directly from the source of truth (DB) for exact ID lookups
        return jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<ProductEntity> getAllProducts() {
        return jpaRepository.findAll();
    }

    @Transactional
    public void deleteProduct(Long id) {
        // 1. Delete from relational database
        jpaRepository.deleteById(id);

        // 2. Delete from OpenSearch index
        searchRepository.deleteById(id);
    }

    // Helper method to keep your mapping logic clean and reusable
    private void syncToOpenSearch(ProductEntity entity) {
        ProductDocument document = new ProductDocument();
        document.setId(entity.getId());
        document.setSku(entity.getSku());
        document.setName(entity.getName());
        document.setDescription(entity.getDescription());
        document.setPrice(entity.getPrice());
        document.setSearchableTags(entity.getName() + " " + entity.getSku());

        searchRepository.save(document);
    }
}