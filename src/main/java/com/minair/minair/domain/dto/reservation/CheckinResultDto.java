package com.minair.minair.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckinResultDto <T>{

    private T seatList;
}
//post 체크인 = 좌석 체크인시 EntityModel에 컬렉션이 담기지 않아 컬렉션을 담는 응답dto를 하나 생성해줘야했음...
//이게 hateoas를 만족시키기 위해 이정도 수고까지 해야하나...라는 생각도 들고..음...이건 경험이 더 쌓이고 나야
//결론이 날것 같다.
