package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>,CustomMemberRepository {

    public Member findByUsername(String username);

}
