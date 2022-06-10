package uz.sicnt.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckLegalRequestDto {
    String
        legal_tin;
    Boolean
        persist;
}