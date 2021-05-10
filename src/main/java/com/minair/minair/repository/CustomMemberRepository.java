package com.minair.minair.repository;

import com.minair.minair.domain.Member;

public interface CustomMemberRepository {

    public int checkId(String id);

    public Member findByRefreshToken(String refreshToken);
}
