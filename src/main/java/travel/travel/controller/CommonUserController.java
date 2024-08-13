package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.travel.apiPayload.ApiResponse;
import travel.travel.dto.user.CommonUserRequest;
import travel.travel.dto.user.CommonUserResponse;
import travel.travel.service.CommonUserService;

@RestController
@RequiredArgsConstructor
public class CommonUserController {

    private final CommonUserService commonUserService;

    @PostMapping("/sign-up")
    public ApiResponse<CommonUserResponse.CommonUserIdResponseDTO> signUp(@RequestBody CommonUserRequest.CommonUserSignUpRequestDTO commonUserSignUpRequestDto) throws Exception {
        CommonUserResponse.CommonUserIdResponseDTO commonUserIdResponseDto = commonUserService.signUp(commonUserSignUpRequestDto);
        return ApiResponse.onSuccess(commonUserIdResponseDto);
    }

    @DeleteMapping("/sign-out")
    public ApiResponse<Void> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        commonUserService.signOut(accessToken);
        return ApiResponse.onSuccess(null);
    }
}
