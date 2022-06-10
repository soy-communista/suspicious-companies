package uz.sicnt.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultDto<T>{
    boolean
        success;
    String
        message;
    T
        data;

}
