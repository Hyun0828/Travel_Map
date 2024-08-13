package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.apiPayload.exception.handler.StoryHandler;
import travel.travel.domain.Story;
import travel.travel.domain.StoryImage;
import travel.travel.repository.StoryImageRepository;
import travel.travel.repository.StoryRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryImageService {

    private final StoryRepository storyRepository;
    private final StoryImageRepository storyImageRepository;

    @Transactional
    public void create(Long storyId, List<MultipartFile> imageFiles) throws IOException {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            Path currentPath = Paths.get("").toAbsolutePath();  // 현재 작업 절대경로
            Path saveImagesPath = currentPath.resolve("saveimages"); // 현재 경로에 save_images 경로 추가

            if (!Files.exists(saveImagesPath)) { // 해당 폴더 없으면
                Files.createDirectories(saveImagesPath); // 생성
            }

            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename(); // 파일 이름 : 고유식별번호 + 원래 이름

                    Path filePath = saveImagesPath.resolve(fileName); // 파일 경로 : 해당 폴더 + 파일 이름
                    imageFile.transferTo(filePath.toFile()); // 파일 경로 => 파일 변환 후 해당 경로에 파일 저장

                    StoryImage storyImage = StoryImage.builder()
                            .imageUrl("/saveimages/" + fileName)
                            .build();
                    story.addImage(storyImage);
                    StoryImage savedStoryImage = storyImageRepository.save(storyImage);
                }
            }
        }
    }

    @Transactional
    public void update(Long storyId, List<MultipartFile> imageFiles) throws IOException {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            Path currentPath = Paths.get("").toAbsolutePath();  // 현재 작업 절대경로
            Path saveImagesPath = currentPath.resolve("saveimages"); // 현재 경로에 save_images 경로 추가

            if (!Files.exists(saveImagesPath)) { // 해당 폴더 없으면
                Files.createDirectories(saveImagesPath); // 생성
            }

            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    String fileName = null;
                    if (Objects.equals(imageFile.getOriginalFilename(), "blob"))
                        fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename(); // 파일 이름 : 고유식별번호 + 원래 이름
                    else
                        fileName = imageFile.getOriginalFilename();
                    Path filePath = saveImagesPath.resolve(fileName); // 파일 경로 : 해당 폴더 + 파일 이름
                    imageFile.transferTo(filePath.toFile()); // 파일 경로 => 파일 변환 후 해당 경로에 파일 저장

                    StoryImage storyImage = StoryImage.builder()
                            .imageUrl("/saveimages/" + fileName)
                            .build();
                    story.addImage(storyImage);
                    StoryImage savedStoryImage = storyImageRepository.save(storyImage);
                }
            }
        }
    }

    @Transactional
    public void delete(Long storyId) {
        List<StoryImage> storyImages = storyImageRepository.findByStoryId(storyId);
        for (StoryImage storyImage : storyImages) {
            Path currentPath = Paths.get("").toAbsolutePath();
            String imageUrl = currentPath + storyImage.getImageUrl();
            File deleteFile = new File(imageUrl);
            deleteFile.delete();
        }
        storyImageRepository.deleteByStoryId(storyId);
    }

    public String uploadImage(Long storyId) {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);

        String imageUrl = story.getImages().get(0).getImageUrl();
        return imageUrl.substring("/saveimages/".length());
    }

    public List<String> uploadImages(Long storyId) {
        Story story = storyRepository.findById(storyId).orElse(null);
        if (story == null)
            throw new StoryHandler(ErrorStatus._STORY_NOT_FOUND);
        List<StoryImage> images = story.getImages();

        List<String> fileNames = images.stream()
                .map(image -> image.getImageUrl().substring("/saveimages/".length()))
                .toList();

        return fileNames;
    }
}
