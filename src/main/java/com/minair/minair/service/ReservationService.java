package com.minair.minair.service;

import com.minair.minair.common.ServerConstValue;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.ReservationGenerateDto;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.reservation.*;
import com.minair.minair.exception.NotFoundReservations;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final AirlineRepository airlineRepository;
    private final ModelMapper modelMapper;
    private final ServerConstValue serverConstValue;

    @Transactional
    public List<ReservationResultDto> reservation(ReservationDto reservationDto) throws RuntimeException{
        log.info("예약 진행!");
        if (reservationDto == null) {
            log.info("요청이 올바르지 않습니다.");
            throw new RequestNullException();
        }
        Optional<Airline> optionalGoAir = airlineRepository.findById(reservationDto.getGoAirId());
        Optional<Airline> optionalBackAir = airlineRepository.findById(reservationDto.getBackAirId());
        Airline goAir = optionalGoAir.get();
        Airline backAir = optionalBackAir.get();
        Member member = memberRepository.findByUsername(reservationDto.getUsername());

        ReservationGenerateDto goReservationDto =
                ReservationGenerateDto.builder()
                        .member(member)
                        .airline(goAir)
                        .adultCount(reservationDto.getAdultCount())
                        .childCount(reservationDto.getChildCount())
                        .totalPerson(reservationDto.getTotalPerson())
                        .totalPrice(reservationDto.getTotalPrice())
                        .build();
        ReservationGenerateDto backReservationDto =
                ReservationGenerateDto.builder()
                        .member(member)
                        .airline(backAir)
                        .adultCount(reservationDto.getAdultCount())
                        .childCount(reservationDto.getChildCount())
                        .totalPerson(reservationDto.getTotalPerson())
                        .totalPrice(reservationDto.getTotalPrice())
                        .build();

        Reservation goReservation = Reservation.createReservation(goReservationDto);
        Reservation backReservation = Reservation.createReservation(backReservationDto);
        ReservationResultDto goReservationResultDto = modelMapper.map(goReservation,ReservationResultDto.class);
        ReservationResultDto backReservationResultDto = modelMapper.map(backReservation,ReservationResultDto.class);

        reservationRepository.save(goReservation);
        reservationRepository.save(backReservation);

        List<ReservationResultDto> reservationList = new ArrayList<>();
        reservationList.add(goReservationResultDto);
        reservationList.add(backReservationResultDto);

        return reservationList;
    }

    public ReservationsResultDto findReservations(String username,int pageNum) throws RuntimeException{

        int offset = pageNum - 1;

        Member findMember = memberRepository.findByUsername(username);
        PageRequest pageRequest = PageRequest.of(offset,serverConstValue.getLimit(), Sort.by(Sort.Direction.DESC,"id"));
        //PageRequest.of의 offset은 디비 적용시 offset * limit의 양으로 들어감. 귯
        Page<Reservation> reservations;
        try {
            reservations = reservationRepository.pageReservations(findMember, pageRequest);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            throw new NotFoundReservations();
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new NotFoundReservations();
        }
        List<ReservationResultApiDto> resultDtos = reservations.getContent().stream()
                .map(reservation -> new ReservationResultApiDto(reservation))
                .collect(Collectors.toList());

        PageDto pageDto  = new PageDto(pageNum, serverConstValue.getLimit(),
                reservations.getTotalElements(),
                reservations.getTotalPages());

        ReservationsResultDto reservationsResultDto = ReservationsResultDto.builder()
                .reservations(resultDtos)
                .pageDto(pageDto)
                .build();

        return reservationsResultDto;
        //이거 페이징 처리 진행하기 위해 로직 들어내야함.
    }

    public ReservationDetailInfoDto findOneReservation(Long reservationId) throws RuntimeException{
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        Reservation findReservation = optionalReservation.get();
        ReservationDetailInfoDto infoDto
                = ReservationDetailInfoDto.ReservationDetailInfoDto(findReservation);

        return infoDto;
    }

    @Transactional
    public void checkSeat(Long reservationId,String seats) throws RuntimeException{
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        Reservation findReservation = optionalReservation.get();
        findReservation.setSeats(seats);
    }

    public ReservationsResultDto findAll(int pageNum){

        int offset = pageNum - 1;

        PageRequest pageRequest = PageRequest.of(offset,10);
        Page<Reservation> allReservation = reservationRepository.pageAllReservation(pageRequest);
        if (allReservation.getContent().isEmpty())
            throw new NullPointerException();

        List<ReservationResultApiDto> resultDtos = allReservation.getContent().stream()
                .map(reservation -> new ReservationResultApiDto(reservation))
                .collect(Collectors.toList());

        PageDto pageDto = new PageDto(pageNum, 10,
                allReservation.getTotalElements(),allReservation.getTotalPages());

        ReservationsResultDto result = ReservationsResultDto.builder()
                .reservations(resultDtos)
                .pageDto(pageDto)
                .build();

        return result;
    }

    @Transactional
    public void remove(Reservation reservation){
        log.info("예약 삭제");

        reservationRepository.delete(reservation);


    }
}
