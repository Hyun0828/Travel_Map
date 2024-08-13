package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.apiPayload.exception.handler.UserHandler;
import travel.travel.domain.CommonUser;
import travel.travel.domain.User;
import travel.travel.domain.UserImage;
import travel.travel.dto.user.CommonUserRequest;
import travel.travel.dto.user.CommonUserResponse;
import travel.travel.jwt.service.JwtService;
import travel.travel.mapper.CommonUserMapper;
import travel.travel.repository.CommonUserRepository;
import travel.travel.repository.RefreshRepository;
import travel.travel.repository.UserImageRepository;
import travel.travel.repository.UserRepository;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class CommonUserService {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final CommonUserRepository commonUserRepository;
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final PasswordEncoder passwordEncoder;

    public CommonUserResponse.CommonUserIdResponseDTO signUp(CommonUserRequest.CommonUserSignUpRequestDTO commonUserSignUpRequestDto) {
        User user = userRepository.findByEmail(commonUserSignUpRequestDto.getEmail()).orElse(null);
        if(user != null)
            throw new UserHandler(ErrorStatus._USER_DUPLICATED);

        CommonUser commonUser = CommonUserMapper.toCommonUserFromCommonUserSignUpRequestDto(commonUserSignUpRequestDto);

        UserImage userImage = UserImage.builder()
                .url("/saveimages/anonymous.png")
                .user(commonUser)
                .build();

        commonUser.setUserImage(userImage);
        commonUser.passwordEncode(passwordEncoder);
        commonUserRepository.save(commonUser);
        userImageRepository.save(userImage);

        return CommonUserResponse.CommonUserIdResponseDTO.builder().userId(commonUser.getId()).build();
    }

    /**
     * @param accessToken 회원 탈퇴 시 DB에서 refreshToken도 전부 삭제
     */
    public void signOut(String accessToken) {
        Optional<String> email = jwtService.extractEmail(accessToken);
        if (email.isPresent()) {
            commonUserRepository.deleteByEmail(email.get());
            refreshRepository.deleteAllByEmail(email.get());
        }
    }
}
