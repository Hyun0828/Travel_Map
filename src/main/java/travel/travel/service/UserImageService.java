package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.apiPayload.exception.handler.AccessTokenHandler;
import travel.travel.apiPayload.exception.handler.UserHandler;
import travel.travel.apiPayload.exception.handler.UserImageHandler;
import travel.travel.domain.User;
import travel.travel.domain.UserImage;
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

    public void update(String accessToken, MultipartFile imageFile) throws IOException {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
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

                User user = userRepository.findByEmail(email).orElse(null);
                if (user == null)
                    throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
                UserImage userImage = userImageRepository.findByUser(user).orElse(null);
                if (userImage == null)
                    throw new UserImageHandler(ErrorStatus._USERIMAGE_NOT_FOUND);
                userImage.updateImage("/saveimages/" + fileName);   // anonymous image로 만들어놨으니까 객체를 만들지 말고 그냥 업데이트만 해준다.
            }
        }
    }


    public String upload(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        String imageUrl = user.getUserImage().getUrl();

        return imageUrl.substring("/saveimages/".length());
    }
}
