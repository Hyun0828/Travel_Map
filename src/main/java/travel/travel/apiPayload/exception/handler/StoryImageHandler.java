package travel.travel.apiPayload.exception.handler;

import travel.travel.apiPayload.code.BaseErrorCode;
import travel.travel.apiPayload.exception.GeneralException;

public class StoryImageHandler extends GeneralException {

    public StoryImageHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
