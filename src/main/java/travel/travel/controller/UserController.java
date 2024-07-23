package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.dto.user.UserInfoRequestDto;
import travel.travel.dto.user.UserInfoResponseDto;
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
    public ResponseEntity<UserInfoResponseDto> info(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UserInfoResponseDto userInfoResponseDto = userService.info(accessToken);
        return ResponseEntity.ok(userInfoResponseDto);
    }

    /**
     * 유저 정보 업데이트하기
     */
    @PostMapping("/info")
    public ResponseEntity<Void> info(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UserInfoRequestDto userInfoRequestDto) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userService.update(accessToken, userInfoRequestDto);
        return ResponseEntity.ok().build();
    }


}
