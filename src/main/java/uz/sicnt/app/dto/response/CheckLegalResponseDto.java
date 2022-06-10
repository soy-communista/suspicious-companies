package uz.sicnt.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckLegalResponseDto {

    CheckPhysicalResponseDto
            director;
    List<CheckPhysicalResponseDto>
            physical_founders;
    List<String>
            legal_founders;
    String
            tin;
}
