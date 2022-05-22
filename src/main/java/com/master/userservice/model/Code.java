package com.master.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Code {

    @Id private String id;
    private String firstCodePiece;
    private String secondCodePiece;
    private SupportedLanguage language;

}
