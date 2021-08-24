package com.minair.minair.controller;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.*;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.service.AirlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping("/airline")
    public String searchAirline(@ModelAttribute("airlineSearchDto") @Valid AirlineSearchDto airlineSearchDto,
                                Errors errors,
                                //@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate comebackDate,
                                Model model){
        if (errors.hasErrors())
            throw new IllegalArgumentException();

        AirlineSearchApiDto airlineSearchApiResult = airlineService.searchAirlines(airlineSearchDto);

        //오는편 조회후 dto로 변환해 View객체로 감싸줌.

        model.addAttribute("searchInfo",airlineSearchDto);
        model.addAttribute("goAirlineList", airlineSearchApiResult.getGoAirlineList());
        model.addAttribute("backAirlineList", airlineSearchApiResult.getBackAirlineList());

        return "/airline/airlinelist";
    }


    @GetMapping("/airline/new")
    public void createAirline(){
        log.info("항공권등록");
    }

    @GetMapping("/airline/airlines")
    public void airlines(Model model,@RequestParam(value = "pageNum", defaultValue = "1") int pageNum){
        log.info("All 항공권 조회");

        if (pageNum == 0)
            throw new RequestNullException();

        QueryAirlinesDto queryAirlinesDto = airlineService.findAllAirline(pageNum);

        model.addAttribute("airlineList",queryAirlinesDto.getAirlineList());
        model.addAttribute("pageMaker",queryAirlinesDto.getPageDto());
    }
}
