package uz.sicnt.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.sicnt.app.dto.response.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckPhysicalRequestDto {
    PersonDto
            person;
}