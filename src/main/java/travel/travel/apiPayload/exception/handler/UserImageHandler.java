package travel.travel.apiPayload.exception.handler;

import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.exception.GeneralException;

public class UserImageHandler extends GeneralException {

    public UserImageHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
