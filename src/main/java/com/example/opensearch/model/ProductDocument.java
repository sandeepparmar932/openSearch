package com.example.opensearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "products_index")
public class ProductDocument {

    // OpenSearch IDs are typically Strings, but Spring Data can map Longs
    @Id
    private Long id;

    // Keyword type is great for exact matches, filtering, and sorting
    @Field(type = FieldType.Keyword)
    private String sku;

    // Text type with an analyzer is great for full-text fuzzy searches
    @Field(type = FieldType.Text, analyzer = "english")
    private String name;

    @Field(type = FieldType.Text, analyzer = "english")
    private String description;

    @Field(type = FieldType.Double)
    private Double price;

    // You can also add fields here that don't exist in your SQL database,
    // like a pre-computed "searchableText" field that combines name, description,
    // and categories into one massive string for easier querying.
    @Field(type = FieldType.Text)
    private String searchableTags;

    // Constructors, Getters, and Setters
}
