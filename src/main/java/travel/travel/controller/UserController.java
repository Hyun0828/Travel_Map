package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.travel.apiPayload.ApiResponse;
import travel.travel.dto.user.UserRequest;
import travel.travel.dto.user.UserResponse;
import travel.travel.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 유저 정보 불러오기
     */
    @GetMapping("/info")
    public ApiResponse<UserResponse.UserInfoResponseDTO> info(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        System.out.println("accessToken = " + accessToken);
        UserResponse.UserInfoResponseDTO userInfoResponseDto = userService.info(accessToken);
        return ApiResponse.onSuccess(userInfoResponseDto);
    }

    /**
     * 유저 정보 업데이트하기
     */
    @PostMapping("/info")
    public ApiResponse<Void> info(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UserRequest.UserInfoRequestDTO userInfoRequestDto) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userService.update(accessToken, userInfoRequestDto);
        return ApiResponse.onSuccess(null);
    }
}
