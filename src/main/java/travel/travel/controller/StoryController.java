package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.dto.story.StoryCreateRequestDto;
import travel.travel.dto.story.StoryInfoResponseDto;
import travel.travel.dto.story.StoryUpdateRequestDto;
import travel.travel.repository.StoryRepository;
import travel.travel.service.StoryImageService;
import travel.travel.service.StoryService;
import travel.travel.service.WritingService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;
    private final StoryImageService storyImageService;
    private final WritingService writingService;
    private final StoryRepository storyRepository;

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
     * 일기 1개 불러오기
     */
    @GetMapping("/story")
    public ResponseEntity<StoryInfoResponseDto> uploadStory(@RequestParam(value = "storyId") Long storyId) {
        StoryInfoResponseDto storyInfoResponseDto = storyService.upload(storyId);
        return ResponseEntity.ok(storyInfoResponseDto);
    }

    /**
     * 페이징해서 일기 불러오기
     */
    @GetMapping("/story/paging")
    public ResponseEntity<List<StoryInfoResponseDto>> getByPaging(@RequestParam(value = "page_number") Integer page_number,
                                                                  @RequestParam(value = "page_size") Integer page_size,
                                                                  @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        List<StoryInfoResponseDto> storyInfoResponseDtos = storyService.getByPaging(accessToken, page_number, page_size);
        return ResponseEntity.ok(storyInfoResponseDtos);
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
     * 일기 개수 반환
     */
    @GetMapping("/story/count")
    public ResponseEntity<Integer> count(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        Integer count = writingService.count(accessToken);
        return ResponseEntity.ok(count);
    }

    /**
     * 일기 이미지 1개만 불러오기
     */
    @GetMapping("/storyImage")
    public ResponseEntity<Resource> uploadImage(@RequestParam(value = "storyId") Long storyId) throws IOException {
        String fileName = storyImageService.uploadImage(storyId);
        final URI image = Paths.get("").toAbsolutePath().resolve("saveimages").resolve(fileName).toUri();
        final UrlResource urlResource = new UrlResource(image);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(urlResource);
    }

    /**
     * 모든 일기 이미지 불러오기
     * 이미지 1개만 보낼 때와는 달리, uri list를 json으로 보내고, 프론트에서 각각을 uri -> blob -> 이미지로 변환
     */
    @GetMapping("/storyImages")
    public ResponseEntity<List<String>> uploadImages(@RequestParam(value = "storyId") Long storyId) {
        List<String> fileNames = storyImageService.uploadImages(storyId);
        List<String> imageUrls = fileNames.stream()
                .map(fileName -> Paths.get("").toAbsolutePath().resolve("saveimages").resolve(fileName).toUri().toString())
                .toList();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }

    /**
     * 일기 수정
     */
    @PutMapping("/story")
    public ResponseEntity<Void> update(@RequestParam(value = "storyId") Long storyId,
                                       @RequestPart("imageFiles") List<MultipartFile> imageFiles,
                                       @RequestPart(value = "requestDto") StoryUpdateRequestDto storyUpdateRequestDto) throws IOException {
        storyService.update(storyId, storyUpdateRequestDto);
        storyImageService.update(storyId, imageFiles);
        return ResponseEntity.ok().build();
    }

    /**
     * 일기 삭제
     */
    @DeleteMapping("/story")
    public ResponseEntity<Void> delete(@RequestParam(value = "storyId") Long storyId) {
        //TODO sharing 삭제
        writingService.delete(storyId);
        storyService.delete(storyId);
        return ResponseEntity.ok().build();
    }
}
