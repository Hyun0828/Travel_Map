package travel.travel.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // RefreshToken 관련 응답
    _REFRESHTOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "REFRESHTOKEN4001", "쿠키에 RefreshToken이 없습니다."),
    _REFRESHTOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, "REFRESHTOKEN4002", "유효하지 않은 RefreshToken입니다."),
    _REFRESHTOKEN_BLACKLIST(HttpStatus.BAD_REQUEST, "REFRESHTOKEN4003", "블랙리스트인 RefreshToken입니다."),


    // User 관련 응답
    _USER_DUPLICATED(HttpStatus.BAD_REQUEST, "USER4001", "중복이메일입니다.");;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}