package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultDTO {
    private int NumberOfResult;
    private List<PartDTO> Parts;

    public SearchResultDTO(int NumberOfResult, List<PartDTO> Parts) {
        this.NumberOfResult = NumberOfResult;
        this.Parts = Parts;
    }
}

