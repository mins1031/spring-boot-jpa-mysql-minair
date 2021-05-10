package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.minair.minair.domain.QMember.*;

public class CustomMemberRepositoryImpl implements CustomMemberRepository{

    private final JPAQueryFactory queryFactory;

    public CustomMemberRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public int checkId(String id) {

        long checkResult = queryFactory
                .select(member.count())
                .from(member)
                .where(member.username.eq(id))
                .fetchCount();
        int realResult = (int) checkResult;

        System.out.println(realResult);
        return realResult;
    }

    @Override
    public Member findByRefreshToken(String refreshToken) {
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.refreshToken().refreshTokenValue.eq(refreshToken))
                .fetchOne();

        return member;
    }


}
