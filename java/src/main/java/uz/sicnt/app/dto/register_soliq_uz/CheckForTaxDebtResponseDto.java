package uz.sicnt.app.dto.register_soliq_uz;

import java.util.List;

public class CheckForTaxDebtResponseDto {

    List<CheckForTaxDebtResponseDebtPerTaxTypeDto> debt_plural;

    public List<CheckForTaxDebtResponseDebtPerTaxTypeDto> getDebt_plural() {
        return debt_plural;
    }

    public void setDebt_plural(List<CheckForTaxDebtResponseDebtPerTaxTypeDto> debt_plural) {
        this.debt_plural = debt_plural;
    }
}