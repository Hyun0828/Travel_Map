package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.dto.user.CommonUserImageRequestDto;
import travel.travel.service.UserImageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserImageController {

    private final UserImageService userImageService;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@ModelAttribute("imageFile") CommonUserImageRequestDto commonUserImageRequestDto,
                                       @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userImageService.upload(accessToken, commonUserImageRequestDto);
        return ResponseEntity.ok().build();
    }
}
