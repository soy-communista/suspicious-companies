package uz.sicnt.app.dto.response;

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
public class GetVatCriterionResponseDto implements Serializable {

    List<GetVatCriterionCriteriasResponseDto> criterias;
    Date dateProcessing;
    Integer label;
    String tin;

}