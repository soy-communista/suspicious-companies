package uz.sicnt.app.dto.individual_dev;

import lombok.Data;

@Data
public class GetPersonalDataByPinflResponseDto {
    public String
            middleName_uz_cyrillic,
            firstName_uz_cyrillic,
            lastName_uz_cyrillic,
            middleName_uz_latin,
            firstName_uz_latin,
            lastName_uz_latin,
            photo,
            pinfl,
            passportSerie,
            passportNumber,
            birthDate,
            gender,
            tin,
            citizenship,
            liveState;
    public GetPersonalDataByPinflResponseDataStatesDto[] states;

}

class GetPersonalDataByPinflResponseDataStatesDto{
    public String
            key,
            val;
}
