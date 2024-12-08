package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchByKeywordRequestBodyDTO {
    private SearchByKeywordMfrNameRequestDTO SearchByKeywordMfrNameRequest;

    public SearchByKeywordRequestBodyDTO(SearchByKeywordMfrNameRequestDTO searchByKeywordMfrNameRequest) {
        this.SearchByKeywordMfrNameRequest = searchByKeywordMfrNameRequest;
    }
}
