package uz.sicnt.app.dto.register_soliq_uz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.sicnt.app.dto.response.PersonDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckForFoundingCompanyHasOver500MTaxDebtRequestDto {
    PersonDto
        person;
}