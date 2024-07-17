package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.domain.User;
import travel.travel.domain.UserImage;
import travel.travel.dto.user.UserImageRequestDto;
import travel.travel.jwt.service.JwtService;
import travel.travel.repository.UserImageRepository;
import travel.travel.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserImageService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final JwtService jwtService;

    public void upload(String accessToken, UserImageRequestDto userImageRequestDto) throws IOException {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new NullPointerException("해당 user가 없습니다"));
        MultipartFile imageFile = userImageRequestDto.getImageFile();

        if (imageFile != null) {
            Path currentPath = Paths.get("").toAbsolutePath();  // 현재 작업 절대경로
            Path saveImagesPath = currentPath.resolve("saveimages"); // 현재 경로에 save_images 경로 추가

            if (!Files.exists(saveImagesPath)) { // 해당 폴더 없으면
                Files.createDirectories(saveImagesPath); // 생성
            }

            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename(); // 파일 이름 : 고유식별번호 + 원래 이름

                Path filePath = saveImagesPath.resolve(fileName); // 파일 경로 : 해당 폴더 + 파일 이름

                imageFile.transferTo(filePath.toFile()); // 파일 경로 => 파일 변환 후 해당 경로에 파일 저장

                User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당 user가 없습니다"));
                UserImage userImage = userImageRepository.findByUser(user);
                userImage.updateImage("/saveimages/" + fileName);   // anonymous image로 만들어놨으니까 객체를 만들지 말고 그냥 업데이트만 해준다.
            }
        }
    }
}
