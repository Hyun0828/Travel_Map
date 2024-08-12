package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.apiPayload.ApiResponse;
import travel.travel.service.UserImageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userImage")
public class CommonUserImageController {

    private final UserImageService userImageService;

    /**
     * 프로필 이미지 업데이트
     */
    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestPart("imageFile") MultipartFile imageFile,
                                    @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userImageService.update(accessToken, imageFile);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 프로필 이미지 불러오기
     */
    @GetMapping("/upload")
    public ApiResponse<String> upload(@RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        String fileName = userImageService.upload(accessToken);
        String imageUrl = "/images/" + fileName;
        return ApiResponse.onSuccess(imageUrl);
    }
}
