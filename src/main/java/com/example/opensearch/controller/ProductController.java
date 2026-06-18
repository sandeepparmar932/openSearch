package com.example.opensearch.controller;

import com.example.opensearch.model.ProductDocument;
import com.example.opensearch.model.ProductEntity;
import com.example.opensearch.service.AdvancedSearchService;
import com.example.opensearch.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;
    private AdvancedSearchService advancedSearchService;

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity productEntity) {
        ProductEntity created = productService.createProduct(productEntity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long id, @RequestBody ProductEntity productEntity) {
        ProductEntity updated = productService.updateProduct(id, productEntity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- OpenSearch Endpoints ---

    /**
     * Advanced Search API using OpenSearch Operations
     * Example: GET /api/products/search?keyword=laptop
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDocument>> searchProducts(@RequestParam String keyword) {
        List<ProductDocument> searchResults = advancedSearchService.advancedSearch(keyword);
        return ResponseEntity.ok(searchResults);
    }
}
