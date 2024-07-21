package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import travel.travel.domain.Role;
import travel.travel.domain.CommonUser;
import travel.travel.domain.UserImage;
import travel.travel.dto.user.CommonUserIdResponseDto;
import travel.travel.dto.user.CommonUserSignUpRequestDto;
import travel.travel.jwt.service.JwtService;
import travel.travel.repository.RefreshRepository;
import travel.travel.repository.UserImageRepository;
import travel.travel.repository.CommonUserRepository;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final CommonUserRepository commonUserRepository;
    private final UserImageRepository userImageRepository;
    private final PasswordEncoder passwordEncoder;

    public CommonUserIdResponseDto signUp(CommonUserSignUpRequestDto commonUserSignUpRequestDto) throws Exception {
        if (commonUserRepository.findByEmail(commonUserSignUpRequestDto.getEmail()).isPresent())
            throw new Exception("중복 이메일입니다");

        CommonUser commonUser = CommonUser.builder()
                .email(commonUserSignUpRequestDto.getEmail())
                .password(commonUserSignUpRequestDto.getPassword())
                .name(commonUserSignUpRequestDto.getName())
                .birth(commonUserSignUpRequestDto.getBirth())
                .gender(commonUserSignUpRequestDto.getGender())
                .age(commonUserSignUpRequestDto.getAge())
                .location(commonUserSignUpRequestDto.getLocation())
                .role(Role.USER)
                .build();

        UserImage userImage = UserImage.builder()
                .url("/saveimages/anonymous.png")
                .commonUser(commonUser)
                .build();

        commonUser.setUserImage(userImage);
        commonUser.passwordEncode(passwordEncoder);
        commonUserRepository.save(commonUser);
        userImageRepository.save(userImage);

        return CommonUserIdResponseDto.builder().userId(commonUser.getId()).build();
    }

    /**
     * @param accessToken
     * 회원 탈퇴 시 DB에서 refreshToken도 전부 삭제
     */
    public void signOut(String accessToken) {
        Optional<String> email = jwtService.extractEmail(accessToken);
        if(email.isPresent()) {
            commonUserRepository.deleteByEmail(email.get());
            refreshRepository.deleteAllByEmail(email.get());
        }
    }
}
