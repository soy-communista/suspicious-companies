package uz.sicnt.app.dto.single_dev;

import uz.sicnt.app.dto.response.CheckPhysicalResponseDto;

import java.util.List;

public class PopulateSuspiciousLegalEntitiesDto {
    String tin;
    CheckPhysicalResponseDto director;
    List<CheckPhysicalResponseDto> physical_founders;

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public CheckPhysicalResponseDto getDirector() {
        return director;
    }

    public void setDirector(CheckPhysicalResponseDto director) {
        this.director = director;
    }

    public List<CheckPhysicalResponseDto> getPhysical_founders() {
        return physical_founders;
    }

    public void setPhysical_founders(List<CheckPhysicalResponseDto> physical_founders) {
        this.physical_founders = physical_founders;
    }
}
