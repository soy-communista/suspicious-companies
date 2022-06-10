package uz.sicnt.app.dto.mehnat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.sicnt.app.dto.response.PersonDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckForDisabledPrisonerMigrantRequestDto {
    PersonDto
        person;
}