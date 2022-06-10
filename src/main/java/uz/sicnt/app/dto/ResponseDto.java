package uz.sicnt.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> implements Serializable {
    private boolean success;
    private String reason;
    private T data;
}