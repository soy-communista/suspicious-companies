package uz.sicnt.app.dto.register_soliq_uz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckForTaxDebtResponseDebtPerTaxTypeDto implements Serializable {

    public String
            tin,
            name,
            ns10Code,
            ns11Code,
            currentDate,
            na2_code,
            na2NameUz,
            na2NameRu,
            nachislen,
            nedoimka,
            penya,
            pereplata,
            uplochen,
            vozvrat,
            zadoljennost;

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getNedoimka() {
        return nedoimka;
    }

    public void setNedoimka(String nedoimka) {
        this.nedoimka = nedoimka;
    }

    public String getPenya() {
        return penya;
    }

    public void setPenya(String penya) {
        this.penya = penya;
    }
}