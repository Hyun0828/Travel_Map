package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.travel.apiPayload.ApiResponse;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.dto.user.CommonUserRequest;
import travel.travel.dto.user.CommonUserResponse;
import travel.travel.service.CommonUserService;

@RestController
@RequiredArgsConstructor
public class CommonUserController {

    private final CommonUserService commonUserService;

    @PostMapping("/sign-up")
    public ApiResponse<CommonUserResponse.CommonUserIdResponseDTO> signUp(@RequestBody CommonUserRequest.CommonUserSignUpRequestDTO commonUserSignUpRequestDto) throws Exception {

        try {
            CommonUserResponse.CommonUserIdResponseDTO commonUserIdResponseDto = commonUserService.signUp(commonUserSignUpRequestDto);
            return ApiResponse.onSuccess(commonUserIdResponseDto);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus._USER_DUPLICATED.getCode(), ErrorStatus._USER_DUPLICATED.getMessage(), null);
        }
    }

    @DeleteMapping("/sign-out")
    public ApiResponse<Void> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        commonUserService.signOut(accessToken);
        return ApiResponse.onSuccess(null);
    }
}
