package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.dto.UserIdResponseDto;
import travel.travel.dto.UserSignUpRequestDto;
import travel.travel.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserIdResponseDto> signUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto) throws Exception {
        UserIdResponseDto userIdResponseDto = userService.signUp(userSignUpRequestDto);
        return ResponseEntity.ok(userIdResponseDto);
    }
}
