package uz.sicnt.app.service;

import org.apache.kafka.common.protocol.types.Field;
import uz.sicnt.app.dto.ResponseDto;
import uz.sicnt.app.dto.ResultDto;
import uz.sicnt.app.dto.mehnat.CheckForDisabledPrisonerMigrantRequestDto;
import uz.sicnt.app.dto.mehnat.CheckForDisabledPrisonerMigrantResponseDto;
import uz.sicnt.app.dto.register_soliq_uz.CheckForFoundingCompanyHasOver500MTaxDebtRequestDto;
import uz.sicnt.app.dto.register_soliq_uz.CheckForManagingCompanyHasOver500MTaxDebtRequestDto;
import uz.sicnt.app.dto.request.GetCompanyManagersTinsRequestDto;
import uz.sicnt.app.dto.request.GetVatCriterionRequestDto;
import uz.sicnt.app.dto.single_dev.*;
import uz.sicnt.app.dto.response.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

public interface CheckCompanyForSuspiciousService {

    CheckResponseDataDto checkForManagingCompanyAlmostBankrupt(CheckForManagingCompanyAlmostBankruptRequestDto dto);
    CheckResponseDataDto checkForManagingCompanyBankrupt(CheckForManagingCompanyBankruptRequestDto dto);
    CheckResponseDataDto checkForFoundingCompanyAlmostBankrupt(CheckForFoundingCompanyAlmostBankruptRequestDto dto);
    CheckResponseDataDto checkForFoundingCompanyBankrupt(CheckForFoundingCompanyBankruptRequestDto dto);
    CheckResponseDataDto checkForManagingCompanyFrequentlyMoving(CheckForManagingCompanyFrequentlyMovingRequestDto dto);
    CheckResponseDataDto checkForFoundingCompanyFrequentlyMoving(CheckForFoundingCompanyFrequentlyMovingRequestDto dto);
    CheckResponseDataDto checkForManagingCompanyHasOver500MTaxDebt(CheckForManagingCompanyHasOver500MTaxDebtRequestDto dto) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException;
    CheckResponseDataDto checkForFoundingCompanyHasOver500MTaxDebt(CheckForFoundingCompanyHasOver500MTaxDebtRequestDto dto) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException;
    CheckResponseDataDto checkForManagingCompanyMergedWithManyCompanies(CheckForManagingCompanyMergedWithManyCompaniesRequestDto dto);
    CheckResponseDataDto checkForFoundingCompanyMergedWithManyCompanies(CheckForFoundingCompanyMergedWithManyCompaniesRequestDto dto);
    CheckResponseDataDto checkForManagingCompanyListedAsSuspicious(CheckForManagingCompanyListedAsSuspiciousRequestDto dto);
    CheckResponseDataDto checkForFoundingCompanyListedAsSuspicious(CheckForFoundingCompanyListedAsSuspiciousRequestDto dto);

    public CheckForDisabledPrisonerMigrantResponseDto checkForDisabledPrisonerMigrant(CheckForDisabledPrisonerMigrantRequestDto dto) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException;

    CheckResponseDataDto checkManagerGreaterThan60(CheckForAgeRequestDto dto);
    CheckResponseDataDto checkFounderGreaterThan60(CheckForAgeRequestDto dto);
    CheckResponseDataDto checkManagerLessThan18(CheckForAgeRequestDto dto);
    CheckResponseDataDto checkFounderLessThan18(CheckForAgeRequestDto dto);

    GetCompanyManagersTinsResponseDto getCompanyManagersTins(GetCompanyManagersTinsRequestDto dto);
    Long getCompanyEntitiesCount();
    List<String> sliceCompaniesByPosition(Long start, Long capacity);
    Boolean populateSuspiciousLegalEntities(List<ResultDto> suspicious_companies, String action_id);
    Boolean insert(ResultDto suspicious_company, String action_id);

    ResponseDto getVatCriterion(GetVatCriterionRequestDto getVatCriterionRequestDto) throws Exception;
    ResponseDto getVatCriterion(List<GetVatCriterionRequestDto> getVatCriterionRequestDtos) throws Exception;
    void addCriterionGroup2(String tin, Integer label, GetVatCriterionResponseDto getVatCriterionResponseDto);

}
