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

    // AccessToken 관련 응답
    _ACCESSTOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "ACCESSTOKEN4001", "응답 헤더에 AccessToken이 없습니다"),
    _ACCESSTOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "ACCESSTOKEN4002", "유효하지 않은 AccessToken입니다"),

    // RefreshToken 관련 응답
    _REFRESHTOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "REFRESHTOKEN4001", "쿠키에 RefreshToken이 없습니다."),
    _REFRESHTOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "REFRESHTOKEN4002", "유효하지 않은 RefreshToken입니다."),
    _REFRESHTOKEN_BLACKLIST(HttpStatus.NOT_FOUND, "REFRESHTOKEN4003", "블랙리스트인 RefreshToken입니다."),

    // User 관련 응답
    _USER_DUPLICATED(HttpStatus.BAD_REQUEST, "USER4001", "중복 이메일입니다."),
    _USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4002", "해당 유저가 없습니다"),

    // User_Image 관련 응답
    _USERIMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "USERIMAGE4001", "해당 이미지가 없습니다"),

    // Story 관련 응답
    _STORY_NOT_FOUND(HttpStatus.NOT_FOUND, "STORY4001", "해당 일기가 없습니다");

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