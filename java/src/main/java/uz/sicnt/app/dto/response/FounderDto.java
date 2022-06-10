package uz.sicnt.app.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FounderDto {
    String
            founder_type;
    PersonDto
            person;
}
