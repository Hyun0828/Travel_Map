package travel.travel.apiPayload.code;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorReasonDTO {

    private boolean isSuccess;
    private String message;
    private String code;
    private HttpStatus httpStatus;
}
