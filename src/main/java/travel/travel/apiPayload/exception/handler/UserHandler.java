package travel.travel.apiPayload.exception.handler;

import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {

    public UserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
