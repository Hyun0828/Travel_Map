package travel.travel.apiPayload.exception.handler;

import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.exception.GeneralException;

public class StoryHandler extends GeneralException {

    public StoryHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
