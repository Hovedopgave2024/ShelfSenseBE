package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MouserResponseDTO {
    private List<String> Errors;
    private SearchResultDTO SearchResults;

    public MouserResponseDTO(List<String> Errors, SearchResultDTO SearchResults) {
        this.Errors = Errors;
        this.SearchResults = SearchResults;
    }
}

