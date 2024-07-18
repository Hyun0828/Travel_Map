package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import travel.travel.domain.Role;
import travel.travel.domain.User;
import travel.travel.domain.UserImage;
import travel.travel.dto.user.UserIdResponseDto;
import travel.travel.dto.user.UserSignUpRequestDto;
import travel.travel.jwt.service.JwtService;
import travel.travel.repository.RefreshRepository;
import travel.travel.repository.UserImageRepository;
import travel.travel.repository.UserRepository;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final PasswordEncoder passwordEncoder;

    public UserIdResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) throws Exception {
        if (userRepository.findByEmail(userSignUpRequestDto.getEmail()).isPresent())
            throw new Exception("중복 이메일입니다");

        User user = User.builder()
                .email(userSignUpRequestDto.getEmail())
                .password(userSignUpRequestDto.getPassword())
                .name(userSignUpRequestDto.getName())
                .birth(userSignUpRequestDto.getBirth())
                .gender(userSignUpRequestDto.getGender())
                .age(userSignUpRequestDto.getAge())
                .location(userSignUpRequestDto.getLocation())
                .role(Role.USER)
                .build();

        UserImage userImage = UserImage.builder()
                .url("/saveimages/anonymous.png")
                .user(user)
                .build();

        user.setUserImage(userImage);
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
        userImageRepository.save(userImage);

        return UserIdResponseDto.builder().userId(user.getId()).build();
    }

    /**
     * @param accessToken
     * 회원 탈퇴 시 DB에서 refreshToken도 전부 삭제
     */
    public void signOut(String accessToken) {
        Optional<String> email = jwtService.extractEmail(accessToken);
        if(email.isPresent()) {
            userRepository.deleteByEmail(email.get());
            refreshRepository.deleteAllByEmail(email.get());
        }
    }
}
