package uz.sicnt.app.dto.my_soliq_uz;

import lombok.Data;

@Data
public class GetPersonalDataByTinResponseDto {
    public String
                    tin,
                    ns10Code,
                    ns10Name,
                    ns11Code,
                    ns11Name,
                    surName,
                    firstName,
                    middleName,
                    birthDate,
                    sex,
                    sexName,
                    passSeries,
                    passNumber,
                    passDate,
                    passOrg,
                    phone,
                    zipCode,
                    address,
                    ns13Code,
                    ns13Name,
                    tinDate,
                    dateModify,
                    isitd,
                    duty,
                    personalNum;
}
