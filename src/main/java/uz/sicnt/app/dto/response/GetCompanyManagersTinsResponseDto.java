package uz.sicnt.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCompanyManagersTinsResponseDto {
    PersonDto
            director;
    List<FounderDto>
            founders;
}