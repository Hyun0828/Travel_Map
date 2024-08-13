package travel.travel.apiPayload.exception.handler;

import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.exception.GeneralException;

public class AccessTokenHandler extends GeneralException {

    public AccessTokenHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
