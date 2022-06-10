package uz.sicnt.app.dto.single_dev;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.sicnt.app.dto.response.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckForManagingCompanyAlmostBankruptRequestDto {
    PersonDto
            person;
}