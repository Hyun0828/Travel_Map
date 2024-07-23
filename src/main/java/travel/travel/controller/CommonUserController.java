package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.dto.user.CommonUserIdResponseDto;
import travel.travel.dto.user.CommonUserSignUpRequestDto;
import travel.travel.repository.RefreshRepository;
import travel.travel.service.CommonUserService;

@RestController
@RequiredArgsConstructor
public class CommonUserController {

    private final CommonUserService commonUserService;
    private final RefreshRepository refreshRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<CommonUserIdResponseDto> signUp(@RequestBody CommonUserSignUpRequestDto commonUserSignUpRequestDto) throws Exception {
        CommonUserIdResponseDto commonUserIdResponseDto = commonUserService.signUp(commonUserSignUpRequestDto);
        return ResponseEntity.ok(commonUserIdResponseDto);
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        commonUserService.signOut(accessToken);
        return ResponseEntity.ok().build();
    }
}
