package uz.sicnt.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import uz.sicnt.app.domain.sc.CriterionGroup2;
import uz.sicnt.app.dto.ResponseDto;
import uz.sicnt.app.dto.ResultDto;
import uz.sicnt.app.dto.register_soliq_uz.CheckForFoundingCompanyHasOver500MTaxDebtRequestDto;
import uz.sicnt.app.dto.register_soliq_uz.CheckForManagingCompanyHasOver500MTaxDebtRequestDto;
import uz.sicnt.app.dto.response.*;
import uz.sicnt.app.dto.request.*;
import uz.sicnt.app.dto.single_dev.*;
import uz.sicnt.app.repository.sc.CriterionGroup2Repository;
import uz.sicnt.app.service.CheckCompanyForSuspiciousService;
import uz.sicnt.app.utils.DateHelper;
import uz.sicnt.app.utils.GuidHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/check-suspicious")
public class CheckController {

    private final CheckCompanyForSuspiciousService checkCompanyForSuspiciousService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckController.class);

    public CheckController(CheckCompanyForSuspiciousService checkCompanyForSuspiciousService) {
        this.checkCompanyForSuspiciousService = checkCompanyForSuspiciousService;
    }

    @RequestMapping(value = "/physical", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto physical(@RequestBody CheckPhysicalRequestDto dto) throws Exception {

        ResultDto result;

        CheckPhysicalResponseDto checkPhysicalResponseDto = new CheckPhysicalResponseDto();
        checkPhysicalResponseDto.setPerson(dto.getPerson());

        List<CheckResponseDataDto> checkResponseDataDto = new ArrayList<>();

        // 1

        CheckForManagingCompanyAlmostBankruptRequestDto checkForManagingCompanyAlmostBankruptRequestDto = new CheckForManagingCompanyAlmostBankruptRequestDto(dto.getPerson());
        CheckResponseDataDto isManagingCompanyAlmostBankrupt = checkCompanyForSuspiciousService.checkForManagingCompanyAlmostBankrupt(checkForManagingCompanyAlmostBankruptRequestDto);
        checkResponseDataDto.add(isManagingCompanyAlmostBankrupt);

        // 2

        CheckForManagingCompanyBankruptRequestDto checkForManagingCompanyBankruptRequestDto = new CheckForManagingCompanyBankruptRequestDto(dto.getPerson());
        CheckResponseDataDto isManagingCompanyBankrupt = checkCompanyForSuspiciousService.checkForManagingCompanyBankrupt(checkForManagingCompanyBankruptRequestDto);
        checkResponseDataDto.add(isManagingCompanyBankrupt);

        // 3

        CheckForFoundingCompanyAlmostBankruptRequestDto checkForFoundingCompanyAlmostBankruptRequestDto = new CheckForFoundingCompanyAlmostBankruptRequestDto(dto.getPerson());
        CheckResponseDataDto isFoundingCompanyAlmostBankrupt = checkCompanyForSuspiciousService.checkForFoundingCompanyAlmostBankrupt(checkForFoundingCompanyAlmostBankruptRequestDto);
        checkResponseDataDto.add(isFoundingCompanyAlmostBankrupt);

        // 4

        CheckForFoundingCompanyBankruptRequestDto checkForFoundingCompanyBankruptRequestDto = new CheckForFoundingCompanyBankruptRequestDto(dto.getPerson());
        CheckResponseDataDto isFoundingCompanyBankrupt = checkCompanyForSuspiciousService.checkForFoundingCompanyBankrupt(checkForFoundingCompanyBankruptRequestDto);
        checkResponseDataDto.add(isFoundingCompanyBankrupt);

        // 5

        CheckForManagingCompanyFrequentlyMovingRequestDto checkForManagingCompanyFrequentlyMovingRequestDto =
                new CheckForManagingCompanyFrequentlyMovingRequestDto(dto.getPerson());
        CheckResponseDataDto isManagingCompanyFrequentlyMoving = checkCompanyForSuspiciousService.checkForManagingCompanyFrequentlyMoving(checkForManagingCompanyFrequentlyMovingRequestDto);
        checkResponseDataDto.add(isManagingCompanyFrequentlyMoving);

        // 6

        CheckForFoundingCompanyFrequentlyMovingRequestDto checkForFoundingCompanyFrequentlyMovingRequestDto =
                new CheckForFoundingCompanyFrequentlyMovingRequestDto(dto.getPerson());
        CheckResponseDataDto isFoundingCompanyFrequentlyMoving = checkCompanyForSuspiciousService.checkForFoundingCompanyFrequentlyMoving(checkForFoundingCompanyFrequentlyMovingRequestDto);
        checkResponseDataDto.add(isFoundingCompanyFrequentlyMoving);

        // 7

        CheckForManagingCompanyHasOver500MTaxDebtRequestDto checkForManagingCompanyHasOver500MTaxDebtRequestDto = new CheckForManagingCompanyHasOver500MTaxDebtRequestDto(dto.getPerson());
        CheckResponseDataDto isManagingCompanyHasOver500MTaxDebt = checkCompanyForSuspiciousService.checkForManagingCompanyHasOver500MTaxDebt(checkForManagingCompanyHasOver500MTaxDebtRequestDto);
        checkResponseDataDto.add(isManagingCompanyHasOver500MTaxDebt);

        // 8

        CheckForFoundingCompanyHasOver500MTaxDebtRequestDto checkForFoundingCompanyHasOver500MTaxDebtRequestDto = new CheckForFoundingCompanyHasOver500MTaxDebtRequestDto(dto.getPerson());
        CheckResponseDataDto isFoundingCompanyHasOver500MTaxDebt = checkCompanyForSuspiciousService.checkForFoundingCompanyHasOver500MTaxDebt(checkForFoundingCompanyHasOver500MTaxDebtRequestDto);
        checkResponseDataDto.add(isFoundingCompanyHasOver500MTaxDebt);

        // 9

        CheckForManagingCompanyMergedWithManyCompaniesRequestDto checkForManagingCompanyMergedWithManyCompaniesRequestDto =
                new CheckForManagingCompanyMergedWithManyCompaniesRequestDto(dto.getPerson());
        CheckResponseDataDto isManagingCompanyMergedWithManyCompanies = checkCompanyForSuspiciousService.checkForManagingCompanyMergedWithManyCompanies(checkForManagingCompanyMergedWithManyCompaniesRequestDto);
        checkResponseDataDto.add(isManagingCompanyMergedWithManyCompanies);

        // 10

        CheckForFoundingCompanyMergedWithManyCompaniesRequestDto checkForFoundingCompanyMergedWithManyCompaniesRequestDto =
                new CheckForFoundingCompanyMergedWithManyCompaniesRequestDto(dto.getPerson());
        CheckResponseDataDto isFoundingCompanyMergedWithManyCompanies = checkCompanyForSuspiciousService.checkForFoundingCompanyMergedWithManyCompanies(checkForFoundingCompanyMergedWithManyCompaniesRequestDto);
        checkResponseDataDto.add(isFoundingCompanyMergedWithManyCompanies);

        // 11

        CheckForManagingCompanyListedAsSuspiciousRequestDto checkForManagingCompanyListedAsSuspiciousRequestDto =
                new CheckForManagingCompanyListedAsSuspiciousRequestDto(dto.getPerson());
        CheckResponseDataDto isManagingCompanyListedAsSuspicious = checkCompanyForSuspiciousService.checkForManagingCompanyListedAsSuspicious(checkForManagingCompanyListedAsSuspiciousRequestDto);
        checkResponseDataDto.add(isManagingCompanyListedAsSuspicious);

        // 12

        CheckForFoundingCompanyListedAsSuspiciousRequestDto checkForFoundingCompanyListedAsSuspiciousRequestDto =
                new CheckForFoundingCompanyListedAsSuspiciousRequestDto(dto.getPerson());
        CheckResponseDataDto isFoundingCompanyListedAsSuspicious = checkCompanyForSuspiciousService.checkForFoundingCompanyListedAsSuspicious(checkForFoundingCompanyListedAsSuspiciousRequestDto);
        checkResponseDataDto.add(isFoundingCompanyListedAsSuspicious);

        // 13 - 18

//        CheckForDisabledPrisonerMigrantRequestDto checkForDisabledPrisonerMigrantRequestDto = new CheckForDisabledPrisonerMigrantRequestDto(dto.getPerson());
//        CheckForDisabledPrisonerMigrantResponseDto checkForDisabledPrisonerMigrantResponseDto = checkCompanyForSuspiciousService.checkForDisabledPrisonerMigrant(checkForDisabledPrisonerMigrantRequestDto);
//
//        if(checkForDisabledPrisonerMigrantResponseDto != null && checkForDisabledPrisonerMigrantResponseDto.getData() != null && checkForDisabledPrisonerMigrantResponseDto.getData().size() > 0){
//            List<CheckResponseDataDto> l = checkForDisabledPrisonerMigrantResponseDto.getData();
//            for (CheckResponseDataDto crdd : l) {
//                checkResponseDataDto.add(crdd);
//            }
//        }

        // 19

        CheckForAgeRequestDto checkForAgeRequestDto = new CheckForAgeRequestDto(dto.getPerson());

        CheckResponseDataDto isManagerGreaterThan60 = checkCompanyForSuspiciousService.checkManagerGreaterThan60(checkForAgeRequestDto);
        checkResponseDataDto.add(isManagerGreaterThan60);

        // 20

        CheckResponseDataDto isFounderGreaterThan60 = checkCompanyForSuspiciousService.checkFounderGreaterThan60(checkForAgeRequestDto);
        checkResponseDataDto.add(isFounderGreaterThan60);

        // 21

        CheckResponseDataDto isManagerLessThan18 = checkCompanyForSuspiciousService.checkManagerLessThan18(checkForAgeRequestDto);
        checkResponseDataDto.add(isManagerLessThan18);

        // 22

        CheckResponseDataDto isFounderLessThan18 = checkCompanyForSuspiciousService.checkFounderLessThan18(checkForAgeRequestDto);
        checkResponseDataDto.add(isFounderLessThan18);

        checkPhysicalResponseDto.setCriterion(checkResponseDataDto);
        result = new ResultDto(true, null, checkPhysicalResponseDto);

        return result;

    }

    @RequestMapping(value = "/legal", method = RequestMethod.POST)
    @ResponseBody
    public ResultDto legal(@RequestBody CheckLegalRequestDto checkLegalRequestDto) throws Exception {

        ResultDto result;

        // todo: some validations

        CheckLegalResponseDto checkLegalResponseDto = new CheckLegalResponseDto();

        GetCompanyManagersTinsRequestDto getCompanyManagersTinsRequestDto = new GetCompanyManagersTinsRequestDto(checkLegalRequestDto.getLegal_tin());
        GetCompanyManagersTinsResponseDto getCompanyManagersTinsResponseDto = checkCompanyForSuspiciousService.getCompanyManagersTins(getCompanyManagersTinsRequestDto);

        if(getCompanyManagersTinsResponseDto != null && getCompanyManagersTinsResponseDto.getDirector() != null){

            // director

            PersonDto director = getCompanyManagersTinsResponseDto.getDirector();

            CheckPhysicalRequestDto dto = new CheckPhysicalRequestDto();
            dto.setPerson(director);

            result = physical(dto);

            if(result != null){
                CheckPhysicalResponseDto director_criterion_results = (CheckPhysicalResponseDto) result.getData();
                checkLegalResponseDto.setDirector(director_criterion_results);
            }

            // physical founders
            List<FounderDto> founders = getCompanyManagersTinsResponseDto.getFounders();
            List<FounderDto>
                    physical_founders = null,
                    legal_founders = null;

            List<CheckPhysicalResponseDto> founder_criterion_results = null;

            if(founders != null && !founders.isEmpty()){

                physical_founders = founders.stream().filter(x -> x.getFounder_type().equals("physical")).collect(Collectors.toList());

                for (FounderDto fd : physical_founders) {
                    dto = new CheckPhysicalRequestDto();
                    dto.setPerson(fd.getPerson());

                    result = physical(dto);

                    if(result != null){
                        CheckPhysicalResponseDto founder_criterion_result = (CheckPhysicalResponseDto) result.getData();

                        if(founder_criterion_results == null)
                            founder_criterion_results = new ArrayList<>();

                        founder_criterion_results.add(founder_criterion_result);
                    }

                }

                checkLegalResponseDto.setPhysical_founders(founder_criterion_results);

            }

            // todo: legal founders
//            checkLegalResponseDto.setLegal_founders(getCompanyManagersTinsResponseDto.getFounders());
            checkLegalResponseDto.setTin(checkLegalRequestDto.getLegal_tin());

        }else{
            LOGGER.info("Director not found - possible inactive company: " +  checkLegalRequestDto.getLegal_tin() + " " + new Date());
        }

        result = new ResultDto(true, null, checkLegalResponseDto);

        // insert suspicious company data
        if(checkLegalRequestDto.getPersist())
            checkCompanyForSuspiciousService.insert(result, GuidHelper.get());

        return result;
    }

//    @Scheduled(initialDelay = 1000 * 5, fixedDelay=Long.MAX_VALUE)
//    @Async
    @RequestMapping(value = "/get-criterion/group-1", method = RequestMethod.GET)
    public void suspicious() throws Exception {
        AtomicReference<Long> counter = new AtomicReference<>(0l);

        String action_id = GuidHelper.get();

        long startTime = System.nanoTime();
        LOGGER.info("IDENTIFY SUSPICIOUS :: Started " + new Date());

        AtomicReference<Long> start = new AtomicReference<>(0l);
        Long totalCount = checkCompanyForSuspiciousService.getCompanyEntitiesCount();

        Long batchSize = 5L;
        Integer semaphoreCount = 2000;
        Semaphore semaphore = new Semaphore(semaphoreCount);

        LOGGER.info("IDENTIFY SUSPICIOUS :: Found " + totalCount);

        while (start.get() < totalCount) {

            try {
                semaphore.acquire();
                final Long id_start = start.get();

                final List<String> ts = checkCompanyForSuspiciousService.sliceCompaniesByPosition(id_start, batchSize);

                new Thread(
                        () -> {
                            String tin = null;

                            LOGGER.info(
                                    "{} : ========================================================== Start work with tins from {} to {}",
                                    Thread.currentThread().getName(),
                                    id_start,
                                    id_start + ts.size()
                            );

                            List<ResultDto> not_yet_suspicious_companies = new ArrayList<>();

                            for (String legal_entity : ts) {
                                CheckLegalRequestDto checkLegalRequestDto = new CheckLegalRequestDto();
                                checkLegalRequestDto.setPersist(Boolean.FALSE);
                                checkLegalRequestDto.setLegal_tin(legal_entity);

                                LOGGER.info("Work: " + checkLegalRequestDto.getLegal_tin()  + " " + counter.get());

                                try {
                                    not_yet_suspicious_companies.add( legal(checkLegalRequestDto) );
                                } catch (Exception e) {
                                    LOGGER.error(e.getMessage() + " : " + tin);
                                }
                                counter.updateAndGet(v -> v + 1);

                                LOGGER.info("Worked: " + checkLegalRequestDto.getLegal_tin()  + " " + counter.get());
                            }

                            Boolean finished = checkCompanyForSuspiciousService.populateSuspiciousLegalEntities(not_yet_suspicious_companies, action_id);

                            semaphore.release();
                        }
                ).start();

                start.updateAndGet(v -> v + ts.size());

            } catch (Throwable t) {
                throw new Error(t);
            }

        }

        while (semaphore.availablePermits() < semaphoreCount) {
            Thread.yield();
        }

        long finishedTime = System.nanoTime();
        LOGGER.info("IDENTIFY SUSPICIOUS :: Finished " + new Date());
        LOGGER.info("IDENTIFY SUSPICIOUS :: nanoseconds took " + (finishedTime - startTime));

    }

//    @RequestMapping(value = "/get-criterion/group-2", method = RequestMethod.POST)
//    public void getVatCriterion(@RequestBody List<GetVatCriterionRequestDto> dtos) throws Exception {
//
//        long startTime = System.nanoTime();
//        LOGGER.info("CRITERION-GROUP-2 :: Started " + new Date());
//
//        AtomicInteger counter = new AtomicInteger(0);
//
//        int i = 0;
//        if(dtos != null && dtos.size() > 0){
//            for (GetVatCriterionRequestDto dto : dtos) {
//
//                ResponseDto rdd = checkCompanyForSuspiciousService.getVatCriterion(dto);
//
//                if(rdd != null && rdd.isSuccess() && rdd.getData() != null){
//                    GetVatCriterionResponseDto d = (GetVatCriterionResponseDto) rdd.getData();
//                    checkCompanyForSuspiciousService.addCriterionGroup2(dto.getTin(), dto.getLabel(),  d);
//                }
//
//                System.out.println(counter.incrementAndGet());
//            }
//        }
//
//        long finishedTime = System.nanoTime();
//        LOGGER.info("CRITERION-GROUP-2 :: Finished " + new Date());
//        LOGGER.info("CRITERION-GROUP-2 :: nanoseconds took " + (finishedTime - startTime));
//
//    }

    @RequestMapping(value = "/get-criterion/group-2", method = RequestMethod.POST)
    public void getVatCriterion(@RequestBody List<GetVatCriterionRequestDto> dtos) throws Exception {

        long startTime = System.nanoTime();
        LOGGER.info("CRITERION-GROUP-2 :: Started " + new Date());

        ResponseDto rdd = checkCompanyForSuspiciousService.getVatCriterion(dtos);

        AtomicInteger counter = new AtomicInteger(0);

        if(rdd.isSuccess() && rdd.getData() != null){
            List<GetVatCriterionResponseDto> plural = (List<GetVatCriterionResponseDto>) rdd.getData();

            for (GetVatCriterionResponseDto single : plural) {
                checkCompanyForSuspiciousService.addCriterionGroup2(single.getTin(), single.getLabel(), single);
            }

            System.out.println(counter.incrementAndGet());
        }

        long finishedTime = System.nanoTime();
        LOGGER.info("CRITERION-GROUP-2 :: Finished " + new Date());
        LOGGER.info("CRITERION-GROUP-2 :: nanoseconds took " + (finishedTime - startTime));

    }

}
