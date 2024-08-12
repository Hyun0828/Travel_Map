package travel.travel.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import travel.travel.apiPayload.code.BaseCode;
import travel.travel.apiPayload.code.status.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    /**
     * isSuccess : Boolean
     * code : String => HTTP status code 외에 세부적인 결과를 담은 code
     * message : String => 설명
     * result : {응답으로 필요한 또 다른 json}
     */

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> of(BaseCode code, T result) {
        return new ApiResponse<>(true, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), result);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}