package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {

    public int checkId(String id);

    public Member findByRefreshToken(String refreshToken);

    Page<Member> findMembers(Pageable pageable);
}
