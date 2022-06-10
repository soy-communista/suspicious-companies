package uz.sicnt.app.dto.mehnat;

import uz.sicnt.app.dto.response.CheckResponseDataDto;

import java.util.List;

public class CheckForDisabledPrisonerMigrantResponseDto {
    
    List<CheckResponseDataDto> data;
    
    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
    
}