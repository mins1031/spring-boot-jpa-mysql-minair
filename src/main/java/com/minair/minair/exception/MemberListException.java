package com.minair.minair.exception;

public class MemberListException extends RuntimeException {
    @Override
    public String getMessage() {
        return "회원이 없거나 회원목록 조회가 실패 했습니다";
    }
}
