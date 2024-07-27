package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.domain.User;
import travel.travel.dto.user.UserInfoRequestDto;
import travel.travel.dto.user.UserInfoResponseDto;
import travel.travel.jwt.service.JwtService;
import travel.travel.mapper.UserMapper;
import travel.travel.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserInfoResponseDto info(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new IllegalStateException("유효하지 않은 토큰입니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당하는 사용자가 없습니다."));
        return UserMapper.toUserInfoResponseDto(user);
    }

    public void update(String accessToken, UserInfoRequestDto userInfoRequestDto) {
        String email = jwtService.extractEmail(accessToken).orElseThrow(() -> new IllegalStateException("유효하지 않은 토큰입니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당하는 사용자가 없습니다."));
        user.update(userInfoRequestDto);
    }
}
