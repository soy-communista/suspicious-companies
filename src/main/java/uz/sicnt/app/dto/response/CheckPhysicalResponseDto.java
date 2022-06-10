package uz.sicnt.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckPhysicalResponseDto {
    List<CheckResponseDataDto>
            criterion;
    PersonDto
            person;
}
