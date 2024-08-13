package travel.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.apiPayload.ApiResponse;
import travel.travel.dto.story.StoryRequest;
import travel.travel.dto.story.StoryResponse;
import travel.travel.repository.StoryRepository;
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
    private final StoryRepository storyRepository;

    /**
     * 일기 작성하기
     */
    @PostMapping("/story")
    public ApiResponse<Void> create(@RequestPart(value = "imageFiles") List<MultipartFile> imageFiles,
                                    @RequestPart(value = "requestDto") StoryRequest.StoryCreateRequestDTO storyCreateRequestDto,
                                    @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        String accessToken = authorizationHeader.replace("Bearer ", "");
        Long storyId = storyService.create(storyCreateRequestDto);
        writingService.save(accessToken, storyId);
        storyImageService.create(storyId, imageFiles);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 일기 1개 불러오기
     */
    @GetMapping("/story")
    public ApiResponse<StoryResponse.StoryInfoResponseDTO> uploadStory(@RequestParam(value = "storyId") Long storyId) {
        StoryResponse.StoryInfoResponseDTO storyInfoResponseDto = storyService.upload(storyId);
        return ApiResponse.onSuccess(storyInfoResponseDto);
    }

    /**
     * 페이징해서 일기 불러오기
     */
    @GetMapping("/story/paging")
    public ApiResponse<List<StoryResponse.StoryInfoResponseDTO>> getByPaging(@RequestParam(value = "page_number") Integer page_number,
                                                                             @RequestParam(value = "page_size") Integer page_size,
                                                                             @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        List<StoryResponse.StoryInfoResponseDTO> storyInfoResponseDtos = storyService.getByPaging(accessToken, page_number, page_size);
        return ApiResponse.onSuccess(storyInfoResponseDtos);
    }

    /**
     * 모든 일기 불러오기
     */
    @GetMapping("/story/all")
    public ApiResponse<List<StoryResponse.StoryInfoResponseDTO>> getAll(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        List<StoryResponse.StoryInfoResponseDTO> storyInfoResponseDtos = storyService.getAll(accessToken);
        return ApiResponse.onSuccess(storyInfoResponseDtos);
    }

    /**
     * 일기 개수 반환
     */
    @GetMapping("/story/count")
    public ApiResponse<Integer> count(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        Integer count = writingService.count(accessToken);
        return ApiResponse.onSuccess(count);
    }

    /**
     * 일기 이미지 1개만 불러오기
     */
    @GetMapping("/storyImage")
    public ApiResponse<String> uploadImage(@RequestParam(value = "storyId") Long storyId) throws IOException {
        String fileName = storyImageService.uploadImage(storyId);
        String imageUrl = "/images/" + fileName;
        return ApiResponse.onSuccess(imageUrl);
    }

    /**
     * 모든 일기 이미지 불러오기
     * http//localhost:8080/images + fileName을 프론트로 보내고
     * html에서 src에 넣어주면.. webconfig에서 이 특정 url에 대해 로컬 저장소를 뒤져서 이미지 전달해줌... 미쳤다..
     */
    @GetMapping("/storyImages")
    public ApiResponse<List<String>> uploadImages(@RequestParam(value = "storyId") Long storyId) {
        List<String> urls = storyImageService.uploadImages(storyId);
        List<String> imageUrls = urls.stream()
                .map(url -> "/images/" + url)
                .toList();
        return ApiResponse.onSuccess(imageUrls);
    }

    /**
     * 일기 수정
     */
    @PutMapping("/story")
    public ApiResponse<Void> update(@RequestParam(value = "storyId") Long storyId,
                                    @RequestPart(value = "imageFiles") List<MultipartFile> imageFiles,
                                    @RequestPart(value = "requestDto") StoryRequest.StoryUpdateRequestDTO storyUpdateRequestDto) throws IOException {
        storyService.update(storyId, storyUpdateRequestDto);
        storyImageService.delete(storyId);
        storyImageService.update(storyId, imageFiles);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 일기 삭제
     */
    @DeleteMapping("/story")
    public ApiResponse<Void> delete(@RequestParam(value = "storyId") Long storyId) {
        writingService.delete(storyId);
        storyImageService.delete(storyId);
        storyService.delete(storyId);
        return ApiResponse.onSuccess(null);
    }
}
