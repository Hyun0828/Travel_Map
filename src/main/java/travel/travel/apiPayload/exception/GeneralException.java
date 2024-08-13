package travel.travel.apiPayload.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.code.ErrorReasonDTO;


/**
 * RuntimeException을 상속받는 이유 : 일반적인 비즈니스 로직들은 check Exception 이 터질 일이 없으니까
 * 그리고 Spring은 언체크 예외, 에러에 대해서는 자동으로 롤백처리하는데 그 이유는 체크 예외는 개발자가 대응할 것이라는 기대 때문
 */
@Getter
@RequiredArgsConstructor
public class GeneralException extends RuntimeException {

    private final BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
