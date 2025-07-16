package com.MADA.mada_SeoulBike.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MyErrorCode {

    INVALID_INPUT("INVALID_INPUT", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_LOGIN_ID("DUPLICATE_LOGIN_ID","이미 존재하는 유저아이디입니다.",HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN","유효 하지 않은 토큰 값입니다.",HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "존재 하지 않은 리프레쉬 토큰입니다." , HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MATCH("PASSWORD_NOT_MATCH","비밀번호가 다릅니다", HttpStatus.BAD_REQUEST);

    private final String code; //예외코드
    private final String message; //예외메시지
    private final HttpStatus status; // Http 상태코드
}
