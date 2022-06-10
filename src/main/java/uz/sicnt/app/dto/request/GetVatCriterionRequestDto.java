package uz.sicnt.app.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVatCriterionRequestDto implements Serializable {

    String
        tin;
    Integer
        label;

    @JsonFormat(pattern = "dd.MM.yyyy", shape = JsonFormat.Shape.STRING, timezone = "Asia/Tashkent")
    Date
        processing_date;

}