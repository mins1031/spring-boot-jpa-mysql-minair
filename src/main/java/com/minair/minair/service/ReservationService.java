package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.ReservationDto;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final AirlineRepository airlineRepository;

    @Transactional
    public List<Reservation> reservation(ReservationDto reservationDto){
        log.info("예약 진행!");
        if (reservationDto == null)
            log.info("요청이 올바르지 않습니다.");
        Optional<Airline> optionalGoAir = airlineRepository.findById(reservationDto.getGoAirId());
        Airline goAir = optionalGoAir.get();
        Optional<Airline> optionalBackAir = airlineRepository.findById(reservationDto.getBackAirId());
        Airline backAir = optionalBackAir.get();
        Member member = memberRepository.findByUsername(reservationDto.getUsername());
        Reservation goReservation = Reservation.createReservation(member,goAir,
                reservationDto.getAdultCount(), reservationDto.getChildCount(),
                reservationDto.getTotalPerson(), reservationDto.getTotalPrice());
        Reservation backReservation = Reservation.createReservation(member,backAir,
                reservationDto.getAdultCount(), reservationDto.getChildCount(),
                reservationDto.getTotalPerson(), reservationDto.getTotalPrice());
        reservationRepository.save(goReservation);
        reservationRepository.save(backReservation);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(goReservation);
        reservationList.add(backReservation);

        return reservationList;
    }

    public Page<Reservation> findReservation(String username,int pageNum){
        System.out.println(username);
        System.out.println(pageNum);

        int offset = pageNum - 1;
        System.out.println(pageNum);

        Member findMember = memberRepository.findByUsername(username);
        PageRequest pageRequest = PageRequest.of(offset,10, Sort.by(Sort.Direction.DESC,"id"));
        //PageRequest.of의 offset은 디비 적용시 offset * limit의 양으로 들어감. 귯
        Page<Reservation> page = reservationRepository.pageReservations(findMember, pageRequest);

        //List<Reservation> reservationList = reservationRepository.findAllByMember(findMember);
        return page;
        //이거 페이징 처리 진행하기 위해 로직 들어내야함.
        //
    }

    public Reservation findOneReservation(Long reservationId){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        Reservation findReservation = optionalReservation.get();
        return findReservation;
    }

    @Transactional
    public void checkSeat(Long reservationId,String seats){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        Reservation findReservation = optionalReservation.get();
        findReservation.setSeats(seats);
    }
}
