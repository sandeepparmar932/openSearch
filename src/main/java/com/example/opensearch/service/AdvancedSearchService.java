package com.example.opensearch.service;

import com.example.opensearch.model.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.core.OpenSearchOperations;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancedSearchService {

    private final OpenSearchOperations openSearchOperations;

    public List<ProductDocument> advancedSearch(String keyword) {
        log.info("Going for the advance search");
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "name", "description")
                        .fuzziness(Fuzziness.AUTO))
                .build();

        SearchHits<ProductDocument> hits =
                openSearchOperations.search(query, ProductDocument.class);


        var data = hits.stream()
                .map(SearchHit::getContent)
                .toList();
        log.info("Data found is {}", data);
        return data;

    }

}
