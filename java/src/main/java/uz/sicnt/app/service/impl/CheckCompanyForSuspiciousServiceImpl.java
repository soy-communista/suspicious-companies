package uz.sicnt.app.service.impl;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import uz.sicnt.app.domain.legalentities.*;
import uz.sicnt.app.domain.individuals.Individual;
import uz.sicnt.app.domain.sc.CriterionGroup2;
import uz.sicnt.app.domain.sc.SuspiciousCompany;
import uz.sicnt.app.dto.ResponseDto;
import uz.sicnt.app.dto.ResultDto;
import uz.sicnt.app.dto.mehnat.CheckForDisabledPrisonerMigrantRequestDto;
import uz.sicnt.app.dto.mehnat.CheckForDisabledPrisonerMigrantResponseDto;
import uz.sicnt.app.dto.mehnat.json.CheckForDisabledPrisonerMigrantRequestJsonDto;
import uz.sicnt.app.dto.mehnat.json.CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto;
import uz.sicnt.app.dto.mehnat.json.CheckForDisabledPrisonerMigrantRequestJsonParamsDto;
import uz.sicnt.app.dto.mehnat.json.CheckForDisabledPrisonerMigrantResponseJsonDto;
import uz.sicnt.app.dto.register_soliq_uz.*;
import uz.sicnt.app.dto.request.GetCompanyManagersTinsRequestDto;
import uz.sicnt.app.dto.request.GetVatCriterionRequestDto;
import uz.sicnt.app.dto.response.*;
import uz.sicnt.app.dto.single_dev.*;
import uz.sicnt.app.repository.legalentities.*;
import uz.sicnt.app.repository.individuals.IndividualRepository;
import uz.sicnt.app.repository.sc.CriterionGroup2Repository;
import uz.sicnt.app.repository.sc.SuspiciousCompanyRepository;
import uz.sicnt.app.service.CheckCompanyForSuspiciousService;
import uz.sicnt.app.utils.GuidHelper;
import uz.sicnt.app.utils.NumberHelper;
import uz.sicnt.app.utils.StringHelper;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class CheckCompanyForSuspiciousServiceImpl implements CheckCompanyForSuspiciousService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckCompanyForSuspiciousServiceImpl.class);

    private final DirectorRepository directorRepository;
    private final CompanyRepository companyRepository;
    private final FounderRepository founderRepository;
    private final RfMailsRepository rfMailsRepository;
    private final NlMergeRepository nlMergeRepository;
    private final CompanyAddressRepository companyAddressRepository;
    private final IndividualRepository individualRepository;
    private final SuspiciousCompanyRepository suspiciousCompanyRepository;
    private final CriterionGroup2Repository criterionGroup2Repository;

    public CheckCompanyForSuspiciousServiceImpl(DirectorRepository directorRepository,
                                                CompanyRepository companyRepository,
                                                FounderRepository founderRepository,
                                                RfMailsRepository rfMailsRepository,
                                                NlMergeRepository nlMergeRepository,
                                                CompanyAddressRepository companyAddressRepository,
                                                IndividualRepository individualRepository,
                                                SuspiciousCompanyRepository suspiciousCompanyRepository, CriterionGroup2Repository criterionGroup2Repository) {
        this.directorRepository = directorRepository;
        this.companyRepository = companyRepository;
        this.founderRepository = founderRepository;
        this.rfMailsRepository = rfMailsRepository;
        this.nlMergeRepository = nlMergeRepository;
        this.companyAddressRepository = companyAddressRepository;
        this.individualRepository = individualRepository;
        this.suspiciousCompanyRepository = suspiciousCompanyRepository;
        this.criterionGroup2Repository = criterionGroup2Repository;
    }

    @Override
    public CheckResponseDataDto checkForManagingCompanyAlmostBankrupt(CheckForManagingCompanyAlmostBankruptRequestDto dto) {

        CheckResponseDataDto isManagingCompanyAlmostBankrupt = new CheckResponseDataDto();

        isManagingCompanyAlmostBankrupt.setCriteria_type("isManagingCompanyAlmostBankrupt");
        isManagingCompanyAlmostBankrupt.setResult(Boolean.FALSE);
        isManagingCompanyAlmostBankrupt.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));
        if (!optionalIndividual.isPresent()) {
            isManagingCompanyAlmostBankrupt.setResult(null);
            isManagingCompanyAlmostBankrupt.setMessage("Could not get pinfl: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagingCompanyAlmostBankrupt;
        }
        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());

        // get directors
        List<Director> directors = null;
        try {
            directors = directorRepository.findAllByPinfl(pinfl);
        } catch (Throwable t) {
            isManagingCompanyAlmostBankrupt.setResult(null);
            isManagingCompanyAlmostBankrupt.setMessage(t.getMessage());
            return isManagingCompanyAlmostBankrupt;
        }

        if (directors != null && !directors.isEmpty()) {
            List<String> director_company_tins = new ArrayList<>();

            for (Director single : directors) {
                director_company_tins.add(single.getCompanyTin());
            }
            // get companies where status is 20 and company tins are directors' director-pinfls
            List<Company> companies = null;
            try {
                companies = companyRepository.findByTinInAndStatus(director_company_tins, 20l);
            } catch (Throwable t) {
                isManagingCompanyAlmostBankrupt.setResult(null);
                isManagingCompanyAlmostBankrupt.setMessage(t.getMessage());
                return isManagingCompanyAlmostBankrupt;
            }
            if (companies != null && !companies.isEmpty()) {
                isManagingCompanyAlmostBankrupt.setResult(Boolean.TRUE);
            }
        }
        return isManagingCompanyAlmostBankrupt;
    }

    @Override
    public CheckResponseDataDto checkForManagingCompanyBankrupt(CheckForManagingCompanyBankruptRequestDto dto) {
        CheckResponseDataDto isManagingCompanyBankrupt = new CheckResponseDataDto();
        isManagingCompanyBankrupt.setCriteria_type("isManagingCompanyBankrupt");
        isManagingCompanyBankrupt.setResult(Boolean.FALSE);
        isManagingCompanyBankrupt.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));
        if (!optionalIndividual.isPresent()) {
            isManagingCompanyBankrupt.setResult(null);
            isManagingCompanyBankrupt.setMessage("Could not get pinfl: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagingCompanyBankrupt;
        }
        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());
        List<Director> directors = null;
        try {
            directors = directorRepository.findAllByPinfl(pinfl);
        } catch (Throwable t) {
            isManagingCompanyBankrupt.setResult(null);
            isManagingCompanyBankrupt.setMessage(t.getMessage());
            return isManagingCompanyBankrupt;
        }
        if (directors != null && !directors.isEmpty()) {
            List<String> director_company_tins = new ArrayList<>();

            for (Director single : directors) {
                director_company_tins.add(single.getCompanyTin());
            }
            // get companies where status is 31 and company tins are directors' director-tins
            List<Company> companies = null;
            try {
                companies = companyRepository.findByTinInAndStatus(director_company_tins, 31l);
            } catch (Throwable t) {
                isManagingCompanyBankrupt.setResult(null);
                isManagingCompanyBankrupt.setMessage(t.getMessage());
                return isManagingCompanyBankrupt;
            }
            if (companies != null && !companies.isEmpty()) {
                isManagingCompanyBankrupt.setResult(Boolean.TRUE);
            }
        }
        return isManagingCompanyBankrupt;
    }

    @Override
    public CheckResponseDataDto checkForFoundingCompanyAlmostBankrupt(CheckForFoundingCompanyAlmostBankruptRequestDto dto) {
        CheckResponseDataDto isFoundingCompanyAlmostBankrupt = new CheckResponseDataDto();
        isFoundingCompanyAlmostBankrupt.setCriteria_type("isFoundingCompanyAlmostBankrupt");
        isFoundingCompanyAlmostBankrupt.setResult(Boolean.FALSE);
        isFoundingCompanyAlmostBankrupt.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));
        if (!optionalIndividual.isPresent()) {
            isFoundingCompanyAlmostBankrupt.setResult(null);
            isFoundingCompanyAlmostBankrupt.setMessage("Could not get pinfl from  db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isFoundingCompanyAlmostBankrupt;
        }
        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());

        // get founders
        List<Founder> founders = null;
        try {
            founders = founderRepository.findAllByPersonId_In(new ArrayList<String>() {
                {
                    add(pinfl);
                }
            });
        } catch (Throwable t) {
            isFoundingCompanyAlmostBankrupt.setResult(null);
            isFoundingCompanyAlmostBankrupt.setMessage(t.getMessage());

            return isFoundingCompanyAlmostBankrupt;
        }
        if (founders != null && !founders.isEmpty()) {
            List<String> founding_company_tins = new ArrayList<>();
            for (Founder single : founders) {
                founding_company_tins.add(single.getCompanyTin());
            }

            // get companies where status is 20 and company tins are founders' founder-tins
            List<Company> companies = null;
            try {
                companies = companyRepository.findByTinInAndStatus(founding_company_tins, 20l);
            } catch (Throwable t) {
                isFoundingCompanyAlmostBankrupt.setResult(null);
                isFoundingCompanyAlmostBankrupt.setMessage(t.getMessage());
                return isFoundingCompanyAlmostBankrupt;
            }
            if (companies != null && !companies.isEmpty()) {
                isFoundingCompanyAlmostBankrupt.setResult(Boolean.TRUE);
            }
        }
        return isFoundingCompanyAlmostBankrupt;
    }

    @Override
    public CheckResponseDataDto checkForFoundingCompanyBankrupt(CheckForFoundingCompanyBankruptRequestDto dto) {
        CheckResponseDataDto isFoundingCompanyBankrupt = new CheckResponseDataDto();
        isFoundingCompanyBankrupt.setCriteria_type("isFoundingCompanyBankrupt");
        isFoundingCompanyBankrupt.setResult(Boolean.FALSE);
        isFoundingCompanyBankrupt.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));
        if (!optionalIndividual.isPresent()) {
            isFoundingCompanyBankrupt.setResult(null);
            isFoundingCompanyBankrupt.setMessage("Could not get pinfl from  db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));

            return isFoundingCompanyBankrupt;
        }
        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());

        // get founders
        List<Founder> founders = null;
        try {
            founders = founderRepository.findAllByPersonId_In(new ArrayList<String>() {
                {
                    add(pinfl);
                }
            });
        } catch (Throwable t) {
            isFoundingCompanyBankrupt.setResult(null);
            isFoundingCompanyBankrupt.setMessage(t.getMessage());

            return isFoundingCompanyBankrupt;
        }

        if (founders != null && !founders.isEmpty()) {
            List<String> founding_company_tins = new ArrayList<>();

            for (Founder single : founders) {
                founding_company_tins.add(single.getCompanyTin());
            }

            // get companies where status is 31 and company tins are founders' founder-tins
            List<Company> companies = null;

            try {
                companies = companyRepository.findByTinInAndStatus(founding_company_tins, 31l);
            } catch (Throwable t) {
                isFoundingCompanyBankrupt.setResult(null);
                isFoundingCompanyBankrupt.setMessage(t.getMessage());

                return isFoundingCompanyBankrupt;
            }

            if (companies != null && !companies.isEmpty()) {
                isFoundingCompanyBankrupt.setResult(Boolean.TRUE);
            }
        }
        return isFoundingCompanyBankrupt;
    }

    @Override
    public CheckResponseDataDto checkForManagingCompanyFrequentlyMoving(CheckForManagingCompanyFrequentlyMovingRequestDto dto) {

        CheckResponseDataDto isManagingCompanyFrequentlyMoving = new CheckResponseDataDto();
        isManagingCompanyFrequentlyMoving.setCriteria_type("isManagingCompanyFrequentlyMoving");
        isManagingCompanyFrequentlyMoving.setResult(Boolean.FALSE);
        isManagingCompanyFrequentlyMoving.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));

        if (!optionalIndividual.isPresent()) {
            isManagingCompanyFrequentlyMoving.setResult(null);
            isManagingCompanyFrequentlyMoving.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagingCompanyFrequentlyMoving;
        }

        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());
        // get directors
        List<Director> directors = null;

        try {
            directors = directorRepository.findAllByPinfl(pinfl);
        } catch (Throwable t) {
            isManagingCompanyFrequentlyMoving.setResult(null);
            isManagingCompanyFrequentlyMoving.setMessage(t.getMessage());
            return isManagingCompanyFrequentlyMoving;
        }

        if (directors != null && !directors.isEmpty()) {
            List<String> director_company_tins = new ArrayList<>();
            for (Director single : directors) {
                director_company_tins.add(single.getCompanyTin());
            }

            // get moved companies within last 12 months based on directors' director-tins
            List<MovedCompanyStatisticsDto> mcss = rfMailsRepository.findByTin(
                    director_company_tins,
                    new java.sql.Date(
                            Date.from(
                                    LocalDate.now()
                                            .minusMonths(12)
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant()
                            ).getTime()
                    )
            );
            if (mcss != null && !mcss.isEmpty()) {
                isManagingCompanyFrequentlyMoving.setResult(Boolean.TRUE);
            }
        }
        return isManagingCompanyFrequentlyMoving;
    }

    @Override
    public CheckResponseDataDto checkForFoundingCompanyFrequentlyMoving(CheckForFoundingCompanyFrequentlyMovingRequestDto dto) {
        CheckResponseDataDto isFoundingCompanyFrequentlyMoving = new CheckResponseDataDto();
        isFoundingCompanyFrequentlyMoving.setCriteria_type("isFoundingCompanyFrequentlyMoving");
        isFoundingCompanyFrequentlyMoving.setResult(Boolean.FALSE);
        isFoundingCompanyFrequentlyMoving.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));
        if (!optionalIndividual.isPresent()) {
            isFoundingCompanyFrequentlyMoving.setResult(null);
            isFoundingCompanyFrequentlyMoving.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));

            return isFoundingCompanyFrequentlyMoving;
        }
        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());

        // get founders
        List<Founder> founders = null;
        try {
            founders = founderRepository.findAllByPersonId_In(new ArrayList<String>() {
                {
                    add(pinfl);
                }
            });
        } catch (Throwable t) {
            isFoundingCompanyFrequentlyMoving.setResult(null);
            isFoundingCompanyFrequentlyMoving.setMessage(t.getMessage());

            return isFoundingCompanyFrequentlyMoving;
        }
        if (founders != null && !founders.isEmpty()) {
            List<String> founder_company_tins = new ArrayList<>();
            for (Founder single : founders) {
                founder_company_tins.add(single.getCompanyTin());
            }

            // get moved companies within last 12 months based on founders' founder-tins
            List<MovedCompanyStatisticsDto> mcss = rfMailsRepository.findByTin(
                    founder_company_tins,
                    new java.sql.Date(
                            Date.from(
                                    LocalDate.now()
                                            .minusMonths(12)
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant()
                            ).getTime()
                    )
            );

            if (mcss != null && !mcss.isEmpty()) {
                isFoundingCompanyFrequentlyMoving.setResult(Boolean.TRUE);
            }
        }
        return isFoundingCompanyFrequentlyMoving;
    }

    @Override
    public CheckResponseDataDto checkForManagingCompanyHasOver500MTaxDebt(CheckForManagingCompanyHasOver500MTaxDebtRequestDto dto) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        CheckResponseDataDto isManagingCompanyHasOver500MTaxDebt = new CheckResponseDataDto();
        isManagingCompanyHasOver500MTaxDebt.setCriteria_type("isManagingCompanyHasOver500MTaxDebt");
        isManagingCompanyHasOver500MTaxDebt.setResult(Boolean.FALSE);
        isManagingCompanyHasOver500MTaxDebt.setMessage(null);
        List<CheckForTaxDebtResponseTinDebtDto> tin_debt_plural = new ArrayList<>();

        // get all companies where this tin is director
        List<CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto> plural = getManagingOrFoundingCompaniesByPinfl(dto.getPerson().getPinfl(), "0");

        if (plural == null || plural.isEmpty()) {
            isManagingCompanyHasOver500MTaxDebt.setResult(null);
            isManagingCompanyHasOver500MTaxDebt.setMessage("Could not get pinfl from mysoliq OR there is manager information for tin: " + dto.getPerson().getPinfl());
            return isManagingCompanyHasOver500MTaxDebt;
        }

        // todo: foreach should be started in order to calc total debt
        for (CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto single : plural) {
            String url = "/company/tax-debt/" + single.getCompany_tin() + "/" + single.getAddressId();

            // Query parameters
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

            // set authorization settings
            HttpHeaders headers = new HttpHeaders();

            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(headers);

            RestTemplate restTemplate = customRestTemplate("singledev", false);

            ResponseEntity<List<CheckForTaxDebtResponseDebtPerTaxTypeDto>> response = null;
            try {
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<List<CheckForTaxDebtResponseDebtPerTaxTypeDto>>() {
                        }
                );
            } catch (Exception e) {

                isManagingCompanyHasOver500MTaxDebt.setMessage("Could not connect OR parse body data from remote service: singledev - tax-debt: " + e.getMessage());
                isManagingCompanyHasOver500MTaxDebt.setResult(null);

                return isManagingCompanyHasOver500MTaxDebt;

            }

            if (response != null && response.getBody() != null) {

                CheckForTaxDebtResponseDto response_dto = new CheckForTaxDebtResponseDto();
                response_dto.setDebt_plural(response.getBody());

                CheckForTaxDebtResponseTinDebtDto tin_debt_single = calculateTaxDebt(response_dto);

                if (tin_debt_single != null) {
                    tin_debt_single.setAddressId(single.getAddressId());

                    tin_debt_plural.add(tin_debt_single);
                }

            } else {
                isManagingCompanyHasOver500MTaxDebt.setMessage("Could not parse body data from remote service: singledev - tax-debt");
                isManagingCompanyHasOver500MTaxDebt.setResult(null);

                return isManagingCompanyHasOver500MTaxDebt;
            }

        }

        float total_debt = 0f;

        for (CheckForTaxDebtResponseTinDebtDto single : tin_debt_plural) {
            total_debt += single.getDebt();
        }

        if (total_debt > 5_000_000f) {
            isManagingCompanyHasOver500MTaxDebt.setMessage(null);
            isManagingCompanyHasOver500MTaxDebt.setResult(Boolean.TRUE);
        }

        return isManagingCompanyHasOver500MTaxDebt;
    }

    @Override
    public CheckResponseDataDto checkForFoundingCompanyHasOver500MTaxDebt(CheckForFoundingCompanyHasOver500MTaxDebtRequestDto dto) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        CheckResponseDataDto isFoundingCompanyHasOver500MTaxDebt = new CheckResponseDataDto();
        isFoundingCompanyHasOver500MTaxDebt.setCriteria_type("isFoundingCompanyHasOver500MTaxDebt");

        isFoundingCompanyHasOver500MTaxDebt.setResult(Boolean.FALSE);
        isFoundingCompanyHasOver500MTaxDebt.setMessage(null);

        List<CheckForTaxDebtResponseTinDebtDto> tin_debt_plural = new ArrayList<>();

        // get all companies where this tin is founder
        List<CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto> plural = getManagingOrFoundingCompaniesByPinfl(dto.getPerson().getPinfl(), "1");

        if (plural == null || plural.isEmpty()) {
            isFoundingCompanyHasOver500MTaxDebt.setResult(null);
            isFoundingCompanyHasOver500MTaxDebt.setMessage("Could not get pinfl from mysoliq OR there is founder information for pinfl: " + dto.getPerson().getPinfl());

            return isFoundingCompanyHasOver500MTaxDebt;
        }

        // todo: foreach should be started in order to calc total debt

        for (CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto single : plural) {
            String url = "/company/tax-debt/" + single.getCompany_tin() + "/" + single.getAddressId();

            // Query parameters
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

            // set authorization settings
            HttpHeaders headers = new HttpHeaders();

            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(headers);

            RestTemplate restTemplate = customRestTemplate("singledev", false);

            ResponseEntity<List<CheckForTaxDebtResponseDebtPerTaxTypeDto>> response = null;

            try {
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<List<CheckForTaxDebtResponseDebtPerTaxTypeDto>>() {
                        }
                );

            } catch (Exception e) {

                isFoundingCompanyHasOver500MTaxDebt.setMessage("Could not connect OR parse body data from remote service: singledev - tax-debt: " + e.getMessage());
                isFoundingCompanyHasOver500MTaxDebt.setResult(null);

                return isFoundingCompanyHasOver500MTaxDebt;

            }

            if (response != null && response.getBody() != null) {

                CheckForTaxDebtResponseDto response_dto = new CheckForTaxDebtResponseDto();
                response_dto.setDebt_plural(response.getBody());

                CheckForTaxDebtResponseTinDebtDto tin_debt_single = calculateTaxDebt(response_dto);

                if (tin_debt_single != null) {
                    tin_debt_single.setAddressId(single.getAddressId());

                    tin_debt_plural.add(tin_debt_single);
                }

            } else {
                isFoundingCompanyHasOver500MTaxDebt.setMessage("Could not parse body data from remote service: singledev - tax-debt");
                isFoundingCompanyHasOver500MTaxDebt.setResult(null);

                return isFoundingCompanyHasOver500MTaxDebt;

            }

        }

        float total_debt = 0f;

        for (CheckForTaxDebtResponseTinDebtDto single : tin_debt_plural) {
            total_debt += single.getDebt();
        }

        if (total_debt > 5_000_000f) {
            isFoundingCompanyHasOver500MTaxDebt.setMessage(null);
            isFoundingCompanyHasOver500MTaxDebt.setResult(Boolean.TRUE);
        }

        return isFoundingCompanyHasOver500MTaxDebt;

    }

    @Override
    public CheckResponseDataDto checkForManagingCompanyMergedWithManyCompanies(CheckForManagingCompanyMergedWithManyCompaniesRequestDto dto) {

        CheckResponseDataDto isManagingCompanyMergedWithManyCompanies = new CheckResponseDataDto();
        isManagingCompanyMergedWithManyCompanies.setCriteria_type("isManagingCompanyMergedWithManyCompanies");
        isManagingCompanyMergedWithManyCompanies.setResult(Boolean.FALSE);
        isManagingCompanyMergedWithManyCompanies.setMessage(null);

        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));

        if (!optionalIndividual.isPresent()) {
            isManagingCompanyMergedWithManyCompanies.setResult(null);
            isManagingCompanyMergedWithManyCompanies.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagingCompanyMergedWithManyCompanies;
        }

        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());
        // get directors
        List<Director> directors = null;

        try {
            directors = directorRepository.findAllByPinfl(pinfl);
        } catch (Throwable t) {
            isManagingCompanyMergedWithManyCompanies.setResult(null);
            isManagingCompanyMergedWithManyCompanies.setMessage(t.getMessage());
            return isManagingCompanyMergedWithManyCompanies;
        }

        if (directors != null && !directors.isEmpty()) {
            List<String> director_company_tins = new ArrayList<>();
            for (Director single : directors) {
                director_company_tins.add(single.getCompanyTin());
            }
            // get merged companies based on directors' director-tins
            List<MovedCompanyStatisticsDto> mcss = nlMergeRepository.findUniqueMergedCompaniesByTins(director_company_tins);
            if (mcss != null && !mcss.isEmpty()) {
                isManagingCompanyMergedWithManyCompanies.setResult(Boolean.TRUE);
            }
        }

        return isManagingCompanyMergedWithManyCompanies;

    }

    @Override
    public CheckResponseDataDto checkForFoundingCompanyMergedWithManyCompanies(CheckForFoundingCompanyMergedWithManyCompaniesRequestDto dto) {
        CheckResponseDataDto isFoundingCompanyMergedWithManyCompanies = new CheckResponseDataDto();
        isFoundingCompanyMergedWithManyCompanies.setCriteria_type("isFoundingCompanyMergedWithManyCompanies");
        isFoundingCompanyMergedWithManyCompanies.setResult(Boolean.FALSE);
        isFoundingCompanyMergedWithManyCompanies.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));

        if (!optionalIndividual.isPresent()) {
            isFoundingCompanyMergedWithManyCompanies.setResult(null);
            isFoundingCompanyMergedWithManyCompanies.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isFoundingCompanyMergedWithManyCompanies;
        }

        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());
        // get founders
        List<Founder> founders = null;
        try {
            founders = founderRepository.findAllByPersonId_In(new ArrayList<String>() {
                {
                    add(pinfl);
                }
            });
        } catch (Throwable t) {
            isFoundingCompanyMergedWithManyCompanies.setResult(null);
            isFoundingCompanyMergedWithManyCompanies.setMessage(t.getMessage());
            return isFoundingCompanyMergedWithManyCompanies;
        }

        if (founders != null && !founders.isEmpty()) {
            List<String> founding_company_tins = new ArrayList<>();
            for (Founder single : founders) {
                founding_company_tins.add(single.getCompanyTin());
            }
            // get merged companies based on directors' director-tins
            List<MovedCompanyStatisticsDto> mcss = nlMergeRepository.findUniqueMergedCompaniesByTins(founding_company_tins);
            if (mcss != null && !mcss.isEmpty()) {
                isFoundingCompanyMergedWithManyCompanies.setResult(Boolean.TRUE);
            }
        }

        return isFoundingCompanyMergedWithManyCompanies;

    }

    @Override
    public CheckResponseDataDto checkForManagingCompanyListedAsSuspicious(CheckForManagingCompanyListedAsSuspiciousRequestDto dto) {

        CheckResponseDataDto isManagingCompanyListedAsSuspicious = new CheckResponseDataDto();
        isManagingCompanyListedAsSuspicious.setCriteria_type("isManagingCompanyListedAsSuspicious");
        isManagingCompanyListedAsSuspicious.setResult(Boolean.FALSE);
        isManagingCompanyListedAsSuspicious.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));

        if (!optionalIndividual.isPresent()) {
            isManagingCompanyListedAsSuspicious.setResult(null);
            isManagingCompanyListedAsSuspicious.setMessage("Could not get pinfl from db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagingCompanyListedAsSuspicious;
        }

        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());
        // get directors
        List<Director> directors = null;
        try {
            directors = directorRepository.findAllByPinfl(pinfl);
        } catch (Throwable t) {
            isManagingCompanyListedAsSuspicious.setResult(null);
            isManagingCompanyListedAsSuspicious.setMessage(t.getMessage());
            return isManagingCompanyListedAsSuspicious;
        }

        if (directors != null && !directors.isEmpty()) {
            List<String> director_company_tins = new ArrayList<>();
            for (Director single : directors) {
                director_company_tins.add(single.getCompanyTin());
            }

            // find whether director's company is in 'suspicious-companies' list
            List<SuspiciousCompany> suspicious = suspiciousCompanyRepository.findAllByTinIn(director_company_tins);
            if (suspicious != null && !suspicious.isEmpty()) {
                isManagingCompanyListedAsSuspicious.setResult(Boolean.TRUE);
            }
        }

        return isManagingCompanyListedAsSuspicious;

    }

    @Override
    public CheckResponseDataDto checkForFoundingCompanyListedAsSuspicious(CheckForFoundingCompanyListedAsSuspiciousRequestDto dto) {
        CheckResponseDataDto isFoundingCompanyListedAsSuspicious = new CheckResponseDataDto();
        isFoundingCompanyListedAsSuspicious.setCriteria_type("isFoundingCompanyListedAsSuspicious");
        isFoundingCompanyListedAsSuspicious.setResult(Boolean.FALSE);
        isFoundingCompanyListedAsSuspicious.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));


        if (!optionalIndividual.isPresent()) {
            isFoundingCompanyListedAsSuspicious.setResult(null);
            isFoundingCompanyListedAsSuspicious.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isFoundingCompanyListedAsSuspicious;
        }

        String pinfl = String.valueOf(optionalIndividual.get().getPinfl());
        // get founders
        List<Founder> founders = null;
        try {
            founders = founderRepository.findAllByPersonId_In(new ArrayList<String>() {
                {
                    add(pinfl);
                }
            });
        } catch (Throwable t) {
            isFoundingCompanyListedAsSuspicious.setResult(null);
            isFoundingCompanyListedAsSuspicious.setMessage(t.getMessage());
            return isFoundingCompanyListedAsSuspicious;
        }

        if (founders != null && !founders.isEmpty()) {
            List<String> founder_company_tins = new ArrayList<>();
            for (Founder single : founders) {
                founder_company_tins.add(single.getCompanyTin());
            }

            // find whether founder's company is in 'suspicious-companies' list
            List<SuspiciousCompany> suspicious = suspiciousCompanyRepository.findAllByTinIn(founder_company_tins);
            if (suspicious != null && !suspicious.isEmpty()) {
                isFoundingCompanyListedAsSuspicious.setResult(Boolean.TRUE);
            }

//            List<CompanyFlag> companyFlags = companyFlagRepository.findAllByCompanyTinInAndTypeIs(founder_company_tins, "SUSPICIOUS");
//            if (companyFlags != null && !companyFlags.isEmpty()) {
//                isFoundingCompanyListedAsSuspicious.setResult(Boolean.TRUE);
//            }

        }

        return isFoundingCompanyListedAsSuspicious;
    }

    @Override
    public CheckForDisabledPrisonerMigrantResponseDto checkForDisabledPrisonerMigrant(CheckForDisabledPrisonerMigrantRequestDto dto) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        CheckForDisabledPrisonerMigrantResponseDto checkForDisabledPrisonerMigrantResponseDto = new CheckForDisabledPrisonerMigrantResponseDto();
        List<CheckResponseDataDto> data = new ArrayList<>();
        if (!StringUtils.isEmpty(dto.getPerson().getPinfl())) {
            CheckForDisabledPrisonerMigrantRequestJsonDto request_dto = new CheckForDisabledPrisonerMigrantRequestJsonDto();
            request_dto.setJsonrpc("2.0");
            request_dto.setId(123456);
            request_dto.setMethod("enst.citizen.fulldata");

            CheckForDisabledPrisonerMigrantRequestJsonParamsDto params = new CheckForDisabledPrisonerMigrantRequestJsonParamsDto();

            CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto paramsBody = new CheckForDisabledPrisonerMigrantRequestJsonParamsBodyDto();
            paramsBody.setPin(dto.getPerson().getPinfl());

            List<String> keys = new ArrayList<>();
            keys.add("migrant");
            keys.add("retiree");
            keys.add("convict");

            paramsBody.setKeys(keys);

            params.setBody(paramsBody);

            request_dto.setParams(params);

            String url = "/api/v1/services";

            // set authorization settings
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiR05LIiwiaWF0IjoxNjE3Mjc1NzE0fQ.Df72akxwSmBdMMoDknrWGNtJlOLz4HqhFzimHecpM9s");
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.ALL));

            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(new Gson().toJson(request_dto), headers);

            RestTemplate restTemplate = customRestTemplate("mehnat", true);

            try {

                CheckForDisabledPrisonerMigrantResponseJsonDto response =
                        restTemplate.postForObject(url, request, CheckForDisabledPrisonerMigrantResponseJsonDto.class);

                if (response != null) {

                    if (response.result != null &&
                            response.result.data != null &&
                            response.result.data.keys != null
                    ) {

                        CheckResponseDataDto d = new CheckResponseDataDto();
                        d.setCriteria_type("isPrisoner");
                        d.setMessage(null);

                        if (response.result.data.keys.convict != null) {

                            if (response.result.data.keys.convict instanceof LinkedHashMap) {
                                if (!((LinkedHashMap) response.result.data.keys.convict).isEmpty())
                                    d.setResult(Boolean.TRUE);
                                else
                                    d.setResult(Boolean.FALSE);
                            }

                        } else {
                            d.setResult(Boolean.FALSE);
                        }

                        data.add(d);
                        checkForDisabledPrisonerMigrantResponseDto.setData(data);


                        d = new CheckResponseDataDto();
                        d.setCriteria_type("isRetiree");
                        d.setMessage(null);

                        if (response.result.data.keys.retiree != null) {

                            if (response.result.data.keys.retiree instanceof LinkedHashMap) {
                                if (!((LinkedHashMap) response.result.data.keys.retiree).isEmpty())
                                    d.setResult(Boolean.TRUE);
                                else
                                    d.setResult(Boolean.FALSE);
                            }

                        } else {
                            d.setResult(Boolean.FALSE);
                        }

                        data.add(d);
                        checkForDisabledPrisonerMigrantResponseDto.setData(data);


                        d = new CheckResponseDataDto();
                        d.setCriteria_type("isMigrant");
                        d.setMessage(null);

                        if (response.result.data.keys.migrant != null) {

                            if (response.result.data.keys.migrant instanceof LinkedHashMap) {
                                if (!((LinkedHashMap) response.result.data.keys.migrant).isEmpty())
                                    d.setResult(Boolean.TRUE);
                                else
                                    d.setResult(Boolean.FALSE);
                            }

                        } else {
                            d.setResult(Boolean.FALSE);
                        }

                        data.add(d);
                        checkForDisabledPrisonerMigrantResponseDto.setData(data);

                    }

                } else {
                    CheckResponseDataDto d = new CheckResponseDataDto();

                    d.setCriteria_type("mehnat.uz");
                    d.setResult(null);
                    d.setMessage("Could not parse body data from remote service: mehnat.uz");

                    data.add(d);

                    checkForDisabledPrisonerMigrantResponseDto.setData(data);
                }

            } catch (Exception e) {
                CheckResponseDataDto d = new CheckResponseDataDto();

                d.setCriteria_type("mehnat.uz");
                d.setResult(null);
                d.setMessage("Could not connect to remote service: mehnat.uz OR data not found for given pinfl: " + dto.getPerson().getPinfl());

                data.add(d);

                checkForDisabledPrisonerMigrantResponseDto.setData(data);
            }

            return checkForDisabledPrisonerMigrantResponseDto;

        } else {

            CheckResponseDataDto d = new CheckResponseDataDto();

            d.setCriteria_type("mehnat.uz");
            d.setResult(null);
            d.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));

            data.add(d);

            checkForDisabledPrisonerMigrantResponseDto.setData(data);

            return checkForDisabledPrisonerMigrantResponseDto;

        }

    }

    @Override
    public CheckResponseDataDto checkManagerGreaterThan60(CheckForAgeRequestDto dto) {

        CheckResponseDataDto isManagerGreaterThan60 = new CheckResponseDataDto();
        isManagerGreaterThan60.setCriteria_type("isManagerGreaterThan60");
        isManagerGreaterThan60.setResult(Boolean.FALSE);
        isManagerGreaterThan60.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));


        if (!optionalIndividual.isPresent()) {
            isManagerGreaterThan60.setResult(null);
            isManagerGreaterThan60.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagerGreaterThan60;
        }
        Individual individual = optionalIndividual.get();

        LocalDate localDate = null;
        Boolean greaterThan60 = null;
        try {
            localDate =
                    new SimpleDateFormat("yyyy-MM-dd").parse(individual.getBirthDate().toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Throwable t) {
            isManagerGreaterThan60.setResult(null);
            isManagerGreaterThan60.setMessage(t.getMessage());
            return isManagerGreaterThan60;
        }

        if (localDate == null) {
            isManagerGreaterThan60.setResult(null);
            isManagerGreaterThan60.setMessage("Null birth date was tried to check for age requirement");
            return isManagerGreaterThan60;
        } else {
            try {
                greaterThan60 = Period.between(localDate, LocalDate.now()).getYears() > 60;
            } catch (Throwable t) {
                isManagerGreaterThan60.setResult(null);
                isManagerGreaterThan60.setMessage("Could not check birth date for age requirement");
                return isManagerGreaterThan60;
            }
        }

        if (greaterThan60) {
            Optional<Director> director_optional = directorRepository.findFirstByPinfl(individual.getPinfl().toString());
            if (director_optional.isPresent()) {
                isManagerGreaterThan60.setResult(Boolean.TRUE);
            } else {
                isManagerGreaterThan60.setResult(null);
                isManagerGreaterThan60.setMessage("Director not found");
            }
        }

        return isManagerGreaterThan60;
    }

    @Override
    public CheckResponseDataDto checkFounderGreaterThan60(CheckForAgeRequestDto dto) {

        CheckResponseDataDto isFounderGreaterThan60 = new CheckResponseDataDto();
        isFounderGreaterThan60.setCriteria_type("isFounderGreaterThan60");
        isFounderGreaterThan60.setResult(Boolean.FALSE);
        isFounderGreaterThan60.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));

        if (!optionalIndividual.isPresent()) {
            isFounderGreaterThan60.setResult(null);
            isFounderGreaterThan60.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isFounderGreaterThan60;
        }
        Individual individual = optionalIndividual.get();

        LocalDate localDate = null;
        Boolean greaterThan60 = null;

        try {
            localDate =
                    new SimpleDateFormat("yyyy-MM-dd").parse(individual.getBirthDate().toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Throwable t) {
            isFounderGreaterThan60.setResult(null);
            isFounderGreaterThan60.setMessage(t.getMessage());
            return isFounderGreaterThan60;
        }

        if (localDate == null) {
            isFounderGreaterThan60.setResult(null);
            isFounderGreaterThan60.setMessage("Null birth date was tried to check for age requirement");
            return isFounderGreaterThan60;
        } else {
            try {
                greaterThan60 = Period.between(localDate, LocalDate.now()).getYears() > 60;
            } catch (Throwable t) {
                isFounderGreaterThan60.setResult(null);
                isFounderGreaterThan60.setMessage("Could not check birth date for age requirement");
                return isFounderGreaterThan60;
            }
        }

        if (greaterThan60) {
            Optional<Founder> founder_optional = founderRepository.findFirstByPersonId(individual.getPinfl().toString());
            if (founder_optional.isPresent()) {
                isFounderGreaterThan60.setResult(Boolean.TRUE);
            } else {
                isFounderGreaterThan60.setResult(null);
                isFounderGreaterThan60.setMessage("Director not found");
            }
        }
        return isFounderGreaterThan60;

    }

    @Override
    public CheckResponseDataDto checkManagerLessThan18(CheckForAgeRequestDto dto) {

        CheckResponseDataDto isManagerLessThan18 = new CheckResponseDataDto();
        isManagerLessThan18.setCriteria_type("isManagerLessThan18");
        isManagerLessThan18.setResult(Boolean.FALSE);
        isManagerLessThan18.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));
        if (!optionalIndividual.isPresent()) {
            isManagerLessThan18.setResult(null);
            isManagerLessThan18.setMessage("Could not get pinfl from mysoliq db: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isManagerLessThan18;
        }
        Individual individual = optionalIndividual.get();

        LocalDate localDate = null;
        Boolean lessThan18 = null;

        try {
            localDate =
                    new SimpleDateFormat("yyyy-MM-dd").parse(individual.getBirthDate().toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Throwable t) {
            isManagerLessThan18.setResult(null);
            isManagerLessThan18.setMessage(t.getMessage());
            return isManagerLessThan18;
        }

        if (localDate == null) {
            isManagerLessThan18.setResult(null);
            isManagerLessThan18.setMessage("Null birth date was tried to check for age requirement");
            return isManagerLessThan18;
        } else {
            try {
                lessThan18 = Period.between(localDate, LocalDate.now()).getYears() < 18;
            } catch (Throwable t) {
                isManagerLessThan18.setResult(null);
                isManagerLessThan18.setMessage("Could not check birth date for age requirement");
                return isManagerLessThan18;
            }

        }

        if (lessThan18) {
            Optional<Director> director_optional = directorRepository.findFirstByPinfl(individual.getPinfl().toString());
            if (director_optional.isPresent()) {
                isManagerLessThan18.setResult(Boolean.TRUE);
            } else {
                isManagerLessThan18.setResult(null);
                isManagerLessThan18.setMessage("Director not found");
            }
        }

        return isManagerLessThan18;

    }

    @Override
    public CheckResponseDataDto checkFounderLessThan18(CheckForAgeRequestDto dto) {

        CheckResponseDataDto isFounderLessThan18 = new CheckResponseDataDto();
        isFounderLessThan18.setCriteria_type("isFounderLessThan18");
        isFounderLessThan18.setResult(Boolean.FALSE);
        isFounderLessThan18.setMessage(null);
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(dto.getPerson().getPinfl()));

        if (!optionalIndividual.isPresent()) {
            isFounderLessThan18.setResult(null);
            isFounderLessThan18.setMessage("Could not get pinfl from mysoliq db for tin: " + NumberHelper.parseLong(dto.getPerson().getPinfl()));
            return isFounderLessThan18;
        }
        Individual individual = optionalIndividual.get();

        LocalDate localDate = null;
        Boolean lessThan18 = null;

        try {
            localDate =
                    new SimpleDateFormat("yyyy-MM-dd").parse(individual.getBirthDate().toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Throwable t) {
            isFounderLessThan18.setResult(null);
            isFounderLessThan18.setMessage(t.getMessage());
            return isFounderLessThan18;
        }

        if (localDate == null) {
            isFounderLessThan18.setResult(null);
            isFounderLessThan18.setMessage("Null birth date was tried to check for age requirement");
            return isFounderLessThan18;
        } else {
            try {
                lessThan18 = Period.between(localDate, LocalDate.now()).getYears() < 18;
            } catch (Throwable t) {
                isFounderLessThan18.setResult(null);
                isFounderLessThan18.setMessage("Could not check birth date for age requirement");
                return isFounderLessThan18;
            }
        }

        if (lessThan18) {
            Optional<Founder> founder_optional = founderRepository.findFirstByPersonId(individual.getPinfl().toString());
            if (founder_optional.isPresent()) {
                isFounderLessThan18.setResult(Boolean.TRUE);
            } else {
                isFounderLessThan18.setResult(null);
                isFounderLessThan18.setMessage("Director not found");
            }
        }
        return isFounderLessThan18;
    }

    public GetCompanyManagersTinsResponseDto getCompanyManagersTins(GetCompanyManagersTinsRequestDto dto) {
        GetCompanyManagersTinsResponseDto getCompanyManagersTins = new GetCompanyManagersTinsResponseDto();

        // director
        PersonDto director = null;

        // founders
        List<FounderDto> founders = null;

        try {
            List<Object[]> managers = directorRepository.getManagers(dto.getTin());

            for (Object[] manager : managers) {
                String pinfl = (String) manager[1];
                String tin = (String) manager[2];
                String occupation = (String) manager[3];

                if (occupation.equals("director")) {
                    director = new PersonDto();

                    director.setPinfl(pinfl);
                    director.setTin(tin);

                } else {

                    FounderDto fd = new FounderDto();

                    if(StringHelper.get(pinfl) != null && pinfl.length() == 14){
                        fd.setFounder_type("physical");
                        fd.setPerson(new PersonDto(
                            pinfl, tin
                        ));
                    } else {
                        fd.setFounder_type("legal");
                        fd.setPerson(new PersonDto(
                            pinfl, tin
                        ));
                    }

                    if(founders == null)
                        founders = new ArrayList<>();

                    founders.add(fd);

                }
                getCompanyManagersTins.setDirector(director);
                getCompanyManagersTins.setFounders(founders);
            }
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            getCompanyManagersTins = null;
        }
        return getCompanyManagersTins;
    }

    public Long getCompanyEntitiesCount() {
        return companyRepository.countAllByStatusIn(new ArrayList<Long>(){{
            add(0L);
            add(11L);
        }});
    }

    public List<String> sliceCompaniesByPosition(Long start, Long capacity) {
        return companyRepository.sliceCompaniesByPosition(start, capacity);
    }

    @Override
    public Boolean populateSuspiciousLegalEntities(List<ResultDto> not_yet_suspicious_companies, String action_id) {
        try {
            List<PopulateSuspiciousLegalEntitiesDto> suspicious_companies = new ArrayList<>();
            for (ResultDto not_yet_suspicious_company_dto : not_yet_suspicious_companies) {
                PopulateSuspiciousLegalEntitiesDto psle = new PopulateSuspiciousLegalEntitiesDto();

                CheckLegalResponseDto not_yet_suspicious_company = (CheckLegalResponseDto) not_yet_suspicious_company_dto.getData();

                if (not_yet_suspicious_company != null) {
                    // set tin
                    psle.setTin(not_yet_suspicious_company.getTin());
                }
                // check director
                if (not_yet_suspicious_company != null && not_yet_suspicious_company.getDirector() != null) {
                    CheckPhysicalResponseDto suspicious_company_director = not_yet_suspicious_company.getDirector();
                    suspicious_company_director.getCriterion().removeIf(checkResponseDataDto -> checkResponseDataDto.getResult() != Boolean.TRUE);
                    if (suspicious_company_director.getCriterion() != null && !suspicious_company_director.getCriterion().isEmpty())
                        psle.setDirector(suspicious_company_director);
                    // can be null if empty criterion OR should have criterion
                }

                // check physical_founders
                if (not_yet_suspicious_company != null && not_yet_suspicious_company.getPhysical_founders() != null && !not_yet_suspicious_company.getPhysical_founders().isEmpty()) {
                    List<CheckPhysicalResponseDto> suspicious_company_founders = not_yet_suspicious_company.getPhysical_founders();
                    if (suspicious_company_founders != null && !suspicious_company_founders.isEmpty()) {
                        Iterator<CheckPhysicalResponseDto> iter = suspicious_company_founders.iterator();
                        while (iter.hasNext()) {
                            CheckPhysicalResponseDto checkPhysicalResponseDto = iter.next();
                            checkPhysicalResponseDto.getCriterion().removeIf(checkResponseDataDto -> checkResponseDataDto.getResult() != Boolean.TRUE);
                            if (checkPhysicalResponseDto.getCriterion().isEmpty()) {
                                iter.remove();
                            }
                        }
                        if (suspicious_company_founders != null && !suspicious_company_founders.isEmpty())
                            psle.setPhysical_founders(suspicious_company_founders);
                    }
                }
                if (
                        !StringUtils.isEmpty(psle.getTin()) &&
                                (psle.getDirector() != null || psle.getPhysical_founders() != null)
                ) {
                    suspicious_companies.add(psle);
                }
            }

            if (suspicious_companies != null && !suspicious_companies.isEmpty()) {
                Iterator<PopulateSuspiciousLegalEntitiesDto> iterator = suspicious_companies.iterator();
                while (iterator.hasNext()) {
                    PopulateSuspiciousLegalEntitiesDto company = iterator.next();
                    String cfc_guid = GuidHelper.get();
                    // insert director
                    if (company.getDirector() != null) {
                        if (company.getDirector().getCriterion() != null && !company.getDirector().getCriterion().isEmpty()) {
                            for (CheckResponseDataDto criteria : company.getDirector().getCriterion()) {

                                SuspiciousCompany sc = new SuspiciousCompany();
                                sc.setActionId(action_id);
                                sc.setCreatedAt(new Date());
                                sc.setTin(company.getTin());
                                sc.setCriteria(criteria.getCriteria_type().toUpperCase());

                                sc.setSubject("director");
                                sc.setPersonTin(company.getDirector().getPerson().getTin());
                                sc.setPersonPinfl(company.getDirector().getPerson().getPinfl());

                                suspiciousCompanyRepository.save(sc);

                            }
                        }
                    }

                    if (company.getPhysical_founders() != null && !company.getPhysical_founders().isEmpty()) {
                        // insert physical founders
                        for (CheckPhysicalResponseDto physical_founder : company.getPhysical_founders()) {
                            List<CheckResponseDataDto> criterion = physical_founder.getCriterion();
                            if (criterion != null && !criterion.isEmpty()) {
                                for (CheckResponseDataDto criteria : criterion) {

                                    SuspiciousCompany sc = new SuspiciousCompany();
                                    sc.setActionId(action_id);
                                    sc.setCreatedAt(new Date());
                                    sc.setTin(company.getTin());
                                    sc.setCriteria(criteria.getCriteria_type().toUpperCase());

                                    sc.setSubject("founder");
                                    sc.setPersonTin(physical_founder.getPerson().getTin());
                                    sc.setPersonPinfl(physical_founder.getPerson().getPinfl());

                                    suspiciousCompanyRepository.save(sc);

                                }
                            }
                        }
                    }

                }
            }
            return Boolean.TRUE;

        } catch (Throwable t) {
            return Boolean.FALSE;
        }
    }

    public Boolean insert(ResultDto nysc, String action_id) {
        try {
            List<PopulateSuspiciousLegalEntitiesDto> suspicious_companies = new ArrayList<>();
            PopulateSuspiciousLegalEntitiesDto psle = new PopulateSuspiciousLegalEntitiesDto();
            CheckLegalResponseDto not_yet_suspicious_company = (CheckLegalResponseDto) nysc.getData();

            if (not_yet_suspicious_company != null) {
                // set tin
                psle.setTin(not_yet_suspicious_company.getTin());
            }

            // check director
            if (not_yet_suspicious_company != null && not_yet_suspicious_company.getDirector() != null) {
                CheckPhysicalResponseDto suspicious_company_director = not_yet_suspicious_company.getDirector();
                suspicious_company_director.getCriterion().removeIf(checkResponseDataDto -> checkResponseDataDto.getResult() != Boolean.TRUE);
                if (suspicious_company_director.getCriterion() != null && !suspicious_company_director.getCriterion().isEmpty())
                    psle.setDirector(suspicious_company_director);
                // can be null if empty criterion OR should have criterion
            }

            // check physical_founders
            if (not_yet_suspicious_company != null && not_yet_suspicious_company.getPhysical_founders() != null && !not_yet_suspicious_company.getPhysical_founders().isEmpty()) {
                List<CheckPhysicalResponseDto> suspicious_company_founders = not_yet_suspicious_company.getPhysical_founders();
                if (suspicious_company_founders != null && !suspicious_company_founders.isEmpty()) {
                    Iterator<CheckPhysicalResponseDto> iter = suspicious_company_founders.iterator();
                    while (iter.hasNext()) {
                        CheckPhysicalResponseDto checkPhysicalResponseDto = iter.next();
                        checkPhysicalResponseDto.getCriterion().removeIf(checkResponseDataDto -> checkResponseDataDto.getResult() != Boolean.TRUE);
                        if (checkPhysicalResponseDto.getCriterion().isEmpty()) {
                            iter.remove();
                        }
                    }
                    if (suspicious_company_founders != null && !suspicious_company_founders.isEmpty())
                        psle.setPhysical_founders(suspicious_company_founders);

                }
            }

            if (
                    !StringUtils.isEmpty(psle.getTin()) &&
                            (psle.getDirector() != null || psle.getPhysical_founders() != null)
            ) {
                suspicious_companies.add(psle);
            }

            if (suspicious_companies != null && !suspicious_companies.isEmpty()) {
                Iterator<PopulateSuspiciousLegalEntitiesDto> iterator = suspicious_companies.iterator();

                while (iterator.hasNext()) {
                    PopulateSuspiciousLegalEntitiesDto company = iterator.next();
                    String cfc_guid = GuidHelper.get();

                    // insert director
                    if (company.getDirector() != null) {
                        if (company.getDirector().getCriterion() != null && !company.getDirector().getCriterion().isEmpty()) {
                            for (CheckResponseDataDto criteria : company.getDirector().getCriterion()) {

                                SuspiciousCompany sc = new SuspiciousCompany();
                                sc.setActionId(action_id);
                                sc.setCreatedAt(new Date());
                                sc.setTin(company.getTin());
                                sc.setCriteria(criteria.getCriteria_type().toUpperCase());

                                sc.setSubject("director");
                                sc.setPersonTin(company.getDirector().getPerson().getTin());
                                sc.setPersonPinfl(company.getDirector().getPerson().getPinfl());

                                suspiciousCompanyRepository.save(sc);

                            }
                        }
                    }

                    if (company.getPhysical_founders() != null && !company.getPhysical_founders().isEmpty()) {
                        // insert physical founders
                        for (CheckPhysicalResponseDto physical_founder : company.getPhysical_founders()) {
                            List<CheckResponseDataDto> criterion = physical_founder.getCriterion();

                            if (criterion != null && !criterion.isEmpty()) {
                                for (CheckResponseDataDto criteria : criterion) {

                                    SuspiciousCompany sc = new SuspiciousCompany();
                                    sc.setActionId(action_id);
                                    sc.setCreatedAt(new Date());
                                    sc.setTin(company.getTin());
                                    sc.setCriteria(criteria.getCriteria_type().toUpperCase());

                                    sc.setSubject("founder");
                                    sc.setPersonTin(physical_founder.getPerson().getTin());
                                    sc.setPersonPinfl(physical_founder.getPerson().getPinfl());

                                    suspiciousCompanyRepository.save(sc);

                                }
                            }
                        }
                    }

                }
            }
            return Boolean.TRUE;
        } catch (Throwable t) {
            return Boolean.FALSE;
        }
    }

    @Override
    public ResponseDto getVatCriterion(GetVatCriterionRequestDto getVatCriterionRequestDto) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        ResponseDto rdd = null;

        String url = "/api/nds-other-api/cabinet/criteria/by-tin?tin=" + getVatCriterionRequestDto.getTin();

        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

        // set authorization settings
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("mySoliqNdsApi", "mys011qndsap1");

        org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(headers);

        RestTemplate restTemplate = customRestTemplate("vat-certificate", false);

        ResponseEntity<GetVatCriterionResponseDto> response = null;

        try {

            response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<GetVatCriterionResponseDto>(){}
            );

            rdd = new ResponseDto();

            rdd.setData(response.getBody());
            rdd.setSuccess(Boolean.TRUE);

        } catch (Exception e){

            rdd = new ResponseDto();

            rdd.setReason("Could not connect to: 'vat-certificate' " + e.getMessage());
            rdd.setSuccess(Boolean.FALSE);

            return rdd;

        }

        return rdd;
    }

    public ResponseDto getVatCriterion(List<GetVatCriterionRequestDto> getVatCriterionRequestDtos) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        List<GetVatCriterionResponseDto> getVatCriterionResponseDtos = new ArrayList<>();
        final Object lock = new Object();

        List<CompletableFuture<List<GetVatCriterionResponseDto>>> futures = new ArrayList<>();

        for (GetVatCriterionRequestDto getVatCriterionRequestDto : getVatCriterionRequestDtos) {
            futures.add(
                    getFuture(getVatCriterionRequestDto.getTin(), getVatCriterionRequestDto.getLabel(),
                            lock, getVatCriterionResponseDtos
                    )
            );
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        return new ResponseDto(Boolean.TRUE, null, getVatCriterionResponseDtos);
    }

    CompletableFuture<List<GetVatCriterionResponseDto>> getFuture(String tin, Integer label, Object lock, List<GetVatCriterionResponseDto> getVatCriterionResponseDtos) {

        return CompletableFuture.supplyAsync(() -> {

            GetVatCriterionResponseDto f = null;

            try {
                f = get(tin);
                f.setLabel(label);
                f.setTin(tin);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(f != null){
                synchronized (lock){
                    getVatCriterionResponseDtos.add(f);
                }
            }
            return getVatCriterionResponseDtos;

        });

    }

    GetVatCriterionResponseDto get(String tin) throws Exception {

        String url = "/api/nds-other-api/cabinet/criteria/by-tin?tin=" + tin;

        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

        // set authorization settings
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("mySoliqNdsApi", "mys011qndsap1");

        org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(headers);

        RestTemplate restTemplate = customRestTemplate("vat-certificate", false);

        ResponseEntity<GetVatCriterionResponseDto> response = null;

        try {

            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<GetVatCriterionResponseDto>(){}
            );

            return response.getBody();

        } catch (Exception e){

            System.out.println(
                    String.format("Cannot get criterion results for %s", tin)
            );

        }

        return null;
    }

    @Override
    public void addCriterionGroup2(String tin, Integer label, GetVatCriterionResponseDto dto) {

        CriterionGroup2 cg2 = new CriterionGroup2();

        cg2.setCreatedAt(new Date());
        cg2.setTin(tin);
        cg2.setLabel(label);

        for (GetVatCriterionCriteriasResponseDto criteria : dto.getCriterias()) {
            if(criteria.getId() == 1){
                cg2.setCriteria1(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 2){
                cg2.setCriteria2(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 3){
                cg2.setCriteria3(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 4){
                cg2.setCriteria4(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 5){
                cg2.setCriteria5(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 6){
                cg2.setCriteria6(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 7){
                cg2.setCriteria7(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 8){
                cg2.setCriteria8(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 9){
                cg2.setCriteria9(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 10){
                cg2.setCriteria10(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 11){
                cg2.setCriteria11(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 12){
                cg2.setCriteria12(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 13){
                cg2.setCriteria13(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 14){
                cg2.setCriteria14(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 15){
                cg2.setCriteria15(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 16){
                cg2.setCriteria16(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 17){
                cg2.setCriteria17(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 18){
                cg2.setCriteria18(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 30){
                cg2.setCriteria30(criteria.getChecked());
                continue;
            }
            else if(criteria.getId() == 31){
                cg2.setCriteria31(criteria.getChecked());
                continue;
            }else{
                System.out.println(String.format("strange criteria: %s %s", tin, criteria.getId()));
            }

        }

        criterionGroup2Repository.save(cg2);

    }

    // Helper functions

    public RestTemplate customRestTemplate(String target, boolean useProxy) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        String baseUrl = "";
        if (target.equals("mehnat")) {
            baseUrl = "https://apigateway.mehnat.uz";
        } else if (target.equals("singledev")) {
            baseUrl = "http://10.15.52.101:11020";
        }
        else if (target.equals("vat-certificate")) {
            baseUrl = "https://vatcertificate.soliq.uz";
        }

        RestTemplate restTemplate = null;
        CloseableHttpClient httpClient = null;

        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
            HttpHost proxy = new HttpHost("192.168.9.100", 3128);
            httpClient = HttpClients
                    .custom()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setSSLSocketFactory(csf)
                    .setProxy(proxy)
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            restTemplate = useProxy ? new RestTemplate(requestFactory) :
                    new RestTemplate();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));

        return restTemplate;
    }

    List<CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto> getManagingOrFoundingCompaniesByPinfl(String pinfl, String manager_type) {

        List<CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto> plural = new ArrayList<>();
        Optional<Individual> optionalIndividual = individualRepository.findFirstByPinfl(NumberHelper.parseLong(pinfl));
        if (!optionalIndividual.isPresent()) {
            return null;
        }

        if (manager_type.equals("0")) {
            // get director by pinfl
            List<Director> directors = directorRepository.findAllByPinfl(pinfl);
            if (directors != null && !directors.isEmpty()) {
                List<String> director_company_tins = new ArrayList<>();
                for (Director single : directors) {
                    director_company_tins.add(single.getCompanyTin());
                }

                // get director company addresses
                List<CompanyAddress> companyAddresses = companyAddressRepository.findByCompanyIn(director_company_tins);
                if (companyAddresses != null && !companyAddresses.isEmpty()) {
                    for (CompanyAddress companyAddress : companyAddresses) {
                        CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto single =
                                new CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto(
                                        companyAddress.getCompany(),
                                        companyAddress.getAddress().getId(),
                                        null,
                                        null,
                                        null
                                );

                        plural.add(single);
                    }

                }

            }

        } else {
            // get founder by pinfl
            List<Founder> founders = founderRepository.findAllByPersonId_In(new ArrayList<String>() {
                {
                    add(pinfl);
                }
            });

            if (founders != null && !founders.isEmpty()) {
                List<String> founder_company_tins = new ArrayList<>();
                for (Founder single : founders) {
                    founder_company_tins.add(single.getCompanyTin());
                }

                // get director company addresses
                List<CompanyAddress> companyAddresses = companyAddressRepository.findByCompanyIn(founder_company_tins);
                if (companyAddresses != null && !companyAddresses.isEmpty()) {
                    for (CompanyAddress companyAddress : companyAddresses) {
                        CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto single =
                                new CheckForTaxDebtGetManagingOrFoundingCompaniesByTinResponseDto(
                                        companyAddress.getCompany(),
                                        companyAddress.getAddress().getId(),
                                        null,
                                        null,
                                        null
                                );
                        plural.add(single);
                    }
                }
            }
        }

        return plural;
    }

    CheckForTaxDebtResponseTinDebtDto calculateTaxDebt(CheckForTaxDebtResponseDto request_dto) {
        CheckForTaxDebtResponseTinDebtDto response_dto = null;
        if (request_dto != null && request_dto.getDebt_plural() != null && !request_dto.getDebt_plural().isEmpty()) {
            response_dto = new CheckForTaxDebtResponseTinDebtDto();
            Float total = 0f;
            String tin = "";
            for (CheckForTaxDebtResponseDebtPerTaxTypeDto single : request_dto.getDebt_plural()) {
                try {
                    Float interest = Float.parseFloat(single.getPenya());
                    Float shortfall = Float.parseFloat(single.getNedoimka());
                    tin = single.getTin();
                    if (interest < 0)
                        total = -interest;
                    else
                        total += interest + shortfall;
                } catch (Exception e) {
                    LOGGER.error("Float parse error: " + new Gson().toJson(single));
                }
            }
            response_dto.setDebt(total);
            response_dto.setCompany_tin(tin);
        }
        return response_dto;
    }

}
