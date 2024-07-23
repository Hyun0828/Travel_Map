package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.dto.user.UserImageRequestDto;
import travel.travel.service.UserImageService;
import java.net.URI;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userImage")
public class CommonUserImageController {

    private final UserImageService userImageService;

    @PostMapping("/update")
    public ResponseEntity<Void> update(@ModelAttribute("imageFile") UserImageRequestDto userImageRequestDto,
                                       @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userImageService.update(accessToken, userImageRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/upload")
    public ResponseEntity<Resource> upload(@RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        String fileName = userImageService.upload(accessToken);
        final URI image = Paths.get("").toAbsolutePath().resolve("saveimages").resolve(fileName).toUri();
        final UrlResource urlResource = new UrlResource(image);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(urlResource);
    }
}
