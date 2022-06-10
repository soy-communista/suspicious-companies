package uz.sicnt.app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetVatCriterionCriteriasResponseDto implements Serializable {

    Integer
            id;
    String
            nameUz,
            nameLat,
            nameRu;
    Integer
            checked;

    @JsonFormat(pattern = "dd.MM.yyyy", shape = JsonFormat.Shape.STRING, timezone = "Asia/Tashkent")
    Date
            dateProcessing;

}