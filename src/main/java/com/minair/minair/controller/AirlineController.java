package com.minair.minair.controller;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.AirlineDto;
import com.minair.minair.domain.dto.airline.AirlineSearchDto;
import com.minair.minair.domain.dto.airline.AirlineSearchResult;
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

        List<Airline> searchAirlineList = airlineService.searchAirlines(airlineSearchDto);
        List<AirlineDto> collect = searchAirlineList.stream()
                .map(a -> new AirlineDto(a.getId(),a.getDeparture(),a.getDistination(),
                        a.getDepartDate(),a.getDepartTime(),a.getReachTime(),
                        a.getAboveSeat()))
                .collect(Collectors.toList());

        AirlineSearchResult<List<AirlineDto>> goAirResult =
                new AirlineSearchResult<List<AirlineDto>>(collect);
        //가는편 조회후 Dto로 변환해 View객체로 감싸줌

        String convertDeparture = airlineSearchDto.getDeparture().toString();
        String convertDistination = airlineSearchDto.getDistination().toString();

        Departure departure = Departure.valueOf(convertDistination);
        Distination distination = Distination.valueOf(convertDeparture);

        AirlineSearchDto backDto = new AirlineSearchDto(departure,distination,
                airlineSearchDto.getComebackDate(), airlineSearchDto.getAdult(), airlineSearchDto.getChild()
        );
        //검색시 오는날 데이터만 따로 담아서 오는편 조회시 가는날 데이터로 넣어서 만들어줌.
        List<Airline> backAirlineList = airlineService.searchAirlines(backDto);
        List<AirlineDto> backCollect = backAirlineList.stream()
                .map(a -> new AirlineDto(a.getId(),a.getDeparture(),a.getDistination(),
                        a.getDepartDate(),a.getDepartTime(),a.getReachTime(),
                        a.getAboveSeat()))
                .collect(Collectors.toList());
        AirlineSearchResult<List<AirlineDto>> backAirResult =
                new AirlineSearchResult<List<AirlineDto>>(backCollect);
        //오는편 조회후 dto로 변환해 View객체로 감싸줌.

        model.addAttribute("searchInfo",airlineSearchDto);
        model.addAttribute("goAirlineList", goAirResult);
        model.addAttribute("backAirlineList", backAirResult);

        return "/airline/airlinelist";
    }

    /*@PostMapping("/airline/new")
    public String createAirline(@RequestBody Airline airline){
        //원래 dto로 받아서 Airline로 바꿔줘야 하지만 일단 테스트기에 받아서 진행
        airlineService.createAirline(airline);
        log.info("항공권 등록!");

        return "항공권 등록!";
    }*/

    @GetMapping("/airline/new")
    public void createAirline(){
        log.info("항공권등록");
    }

    @GetMapping("/airline/airlines")
    public void airlines(Model model,@RequestParam(value = "pageNum", defaultValue = "1") int pageNum){
        log.info("All 항공권 조회");

        if (pageNum == 0)
            throw new RequestNullException();

        Page<Airline> allAirline = airlineService.findAllAirline(pageNum);
        List<AirlineDto> airlineDtoList = allAirline.getContent().stream()
                .map(a -> new AirlineDto(a.getId(),a.getDeparture(),a.getDistination(),
                        a.getDepartDate(),a.getDepartTime(),a.getReachTime(),
                        a.getAboveSeat()))
                .collect(Collectors.toList());
        PageDto pageDto = new PageDto(pageNum,10,allAirline.getTotalElements()
                ,allAirline.getTotalPages());
        // 05/09 PageDto의 offset을 바꿔주는 것이 좋음. 저번에 했을떄 왜 안되었냐면
        // if(offset <= 0) {this.offset = 1} else this.offset = offset 이경우 endPage 공식의 offset값을
        // this.offset을 해야하는데 offset으로 했기 때문일것 내일 적용해보기기
        model.addAttribute("airlineList",airlineDtoList);
        model.addAttribute("pageMaker",pageDto);
    }
}
