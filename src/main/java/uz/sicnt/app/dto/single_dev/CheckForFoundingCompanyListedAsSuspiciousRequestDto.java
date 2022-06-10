package uz.sicnt.app.dto.single_dev;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.sicnt.app.dto.response.PersonDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckForFoundingCompanyListedAsSuspiciousRequestDto {
    PersonDto
            person;
}