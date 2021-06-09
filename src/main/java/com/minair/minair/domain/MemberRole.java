package com.minair.minair.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum MemberRole {
    MEMBER("ROLE_MEMBER","일반 회원 권한"),
    ADMIN("ROLE_ADMIN","관리자 권한");

    private final String roleName;

    private final String description;

    private List<String> getNameList() {
        return Stream.of(MemberRole.values()).map(Enum::name).collect(Collectors.toList());
    }
}
