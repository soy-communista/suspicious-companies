package uz.sicnt.app.dto.register_soliq_uz;

public class CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto {
    String company_tin;
    String addressId;
    String pinfl;
    String tin;
    String occupation;

    public CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto(String company_tin, String addressId, String pinfl, String tin, String occupation) {
        this.company_tin = company_tin;
        this.addressId = addressId;
        this.pinfl = pinfl;
        this.tin = tin;
        this.occupation = occupation;
    }

    public String getCompany_tin() {
        return company_tin;
    }

    public void setCompany_tin(String company_tin) {
        this.company_tin = company_tin;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}