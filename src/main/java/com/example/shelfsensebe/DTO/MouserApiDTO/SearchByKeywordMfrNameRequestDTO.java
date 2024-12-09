package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchByKeywordMfrNameRequestDTO {
    private String manufacturerName;
    private String keyword;
    private int records;
    private int pageNumber;
    private String searchOptions;
    private String searchWithYourSignUpLanguage;

    public SearchByKeywordMfrNameRequestDTO(String manufacturerName, String keyword, int records, int pageNumber,
                                            String searchOptions, String searchWithYourSignUpLanguage) {
        this.manufacturerName = manufacturerName;
        this.keyword = keyword;
        this.records = records;
        this.pageNumber = pageNumber;
        this.searchOptions = searchOptions;
        this.searchWithYourSignUpLanguage = searchWithYourSignUpLanguage;
    }
}

