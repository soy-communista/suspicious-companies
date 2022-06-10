package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

public class MovedCompanyStatisticsDto {
    String tin;
    Long count;

    public MovedCompanyStatisticsDto(String tin, Long count) {
        this.tin = tin;
        this.count = count;
    }
}