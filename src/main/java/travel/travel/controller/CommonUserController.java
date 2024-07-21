package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.dto.user.CommonUserIdResponseDto;
import travel.travel.dto.user.CommonUserSignUpRequestDto;
import travel.travel.repository.RefreshRepository;
import travel.travel.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshRepository refreshRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<CommonUserIdResponseDto> signUp(@RequestBody CommonUserSignUpRequestDto commonUserSignUpRequestDto) throws Exception {
        CommonUserIdResponseDto commonUserIdResponseDto = userService.signUp(commonUserSignUpRequestDto);
        return ResponseEntity.ok(commonUserIdResponseDto);
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userService.signOut(accessToken);
        return ResponseEntity.ok().build();
    }
}
