package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.dto.story.StoryCreateRequestDto;
import travel.travel.service.StoryImageService;
import travel.travel.service.StoryService;
import travel.travel.service.WritingService;

import java.io.IOException;
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
}
