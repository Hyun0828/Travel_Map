package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.dto.story.StoryCreateRequestDto;
import travel.travel.dto.story.StoryInfoResponseDto;
import travel.travel.service.StoryImageService;
import travel.travel.service.StoryService;
import travel.travel.service.WritingService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;
    private final StoryImageService storyImageService;
    private final WritingService writingService;

    /**
     * 일기 작성하기
     */
    @PostMapping("/story")
    public ResponseEntity<Void> create(@RequestPart("imageFiles") List<MultipartFile> imageFiles,
                                       @RequestPart(value = "requestDto") StoryCreateRequestDto storyCreateRequestDto,
                                       @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        Long storyId = storyService.create(accessToken, storyCreateRequestDto);
        writingService.save(accessToken, storyId);
        storyImageService.update(storyId, imageFiles);
        return ResponseEntity.ok().build();
    }

    /**
     * 일기 불러오기
     */
    @GetMapping("/story")
    public ResponseEntity<StoryInfoResponseDto> upload(@RequestParam(value = "storyId") Long storyId) {
        StoryInfoResponseDto storyInfoResponseDto = storyService.upload(storyId);
        return ResponseEntity.ok(storyInfoResponseDto);
    }

    /**
     * 모든 일기 불러오기
     */
    @GetMapping("/story/all")
    public ResponseEntity<List<StoryInfoResponseDto>> getAll(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        List<StoryInfoResponseDto> storyInfoResponseDtos = storyService.getAll(accessToken);
        return ResponseEntity.ok(storyInfoResponseDtos);
    }

    /**
     * 일기 이미지 불러오기
     * 이미지 1개만 보낼 때와는 달리, uri list를 json으로 보내고, 프론트에서 각각을 uri -> blob -> 이미지로 변환
     */
    @GetMapping("/storyImage")
    public ResponseEntity<List<String>> imageUpload(@RequestPart("storyId") Long storyId) throws IOException {
        List<String> fileNames = storyImageService.upload(storyId);
        List<String> imageUrls = fileNames.stream()
                .map(fileName -> Paths.get("").toAbsolutePath().resolve("saveimages").resolve(fileName).toUri().toString())
                .toList();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }
}
