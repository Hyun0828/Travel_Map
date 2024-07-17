package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.dto.user.UserIdResponseDto;
import travel.travel.dto.user.UserImageRequestDto;
import travel.travel.dto.user.UserSignUpRequestDto;
import travel.travel.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserIdResponseDto> signUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto) throws Exception {
        UserIdResponseDto userIdResponseDto = userService.signUp(userSignUpRequestDto);
        return ResponseEntity.ok(userIdResponseDto);
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userService.signOut(accessToken);
        return ResponseEntity.ok().build();
    }
}
