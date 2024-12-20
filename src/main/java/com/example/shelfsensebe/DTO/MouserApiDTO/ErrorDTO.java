package com.example.shelfsensebe.DTO.MouserApiDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDTO {
    private int Id;
    private String Code;
    private String Message;
    private String ResourceKey;
    private String ResourceFormatString;
    private String ResourceFormatString2;
    private String PropertyName;

    public ErrorDTO(int Id, String Code, String Message, String ResourceKey,
                    String ResourceFormatString, String ResourceFormatString2,
                    String PropertyName) {
        this.Id = Id;
        this.Code = Code;
        this.Message = Message;
        this.ResourceKey = ResourceKey;
        this.ResourceFormatString = ResourceFormatString;
        this.ResourceFormatString2 = ResourceFormatString2;
        this.PropertyName = PropertyName;
    }
}
