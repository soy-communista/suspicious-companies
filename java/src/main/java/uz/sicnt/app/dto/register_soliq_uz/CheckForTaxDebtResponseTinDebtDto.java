package uz.sicnt.app.dto.register_soliq_uz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckForTaxDebtResponseTinDebtDto implements Serializable {

    String company_tin;
    String addressId;
    Float debt;

    public String getCompany_tin() {
        return company_tin;
    }

    public void setCompany_tin(String company_tin) {
        this.company_tin = company_tin;
    }

    public Float getDebt() {
        return debt;
    }

    public void setDebt(Float debt) {
        this.debt = debt;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

}