package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.apiPayload.code.status.ErrorStatus;
import travel.travel.apiPayload.exception.handler.AccessTokenHandler;
import travel.travel.apiPayload.exception.handler.UserHandler;
import travel.travel.domain.User;
import travel.travel.dto.user.UserRequest;
import travel.travel.dto.user.UserResponse;
import travel.travel.jwt.service.JwtService;
import travel.travel.mapper.UserMapper;
import travel.travel.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserResponse.UserInfoResponseDTO info(String accessToken) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        return UserMapper.toUserInfoResponseDto(user);
    }

    public void update(String accessToken, UserRequest.UserInfoRequestDTO userInfoRequestDto) {
        String email = jwtService.extractEmail(accessToken).orElse(null);
        if (email == null)
            throw new AccessTokenHandler(ErrorStatus._ACCESSTOKEN_NOT_VALID);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            throw new UserHandler(ErrorStatus._USER_NOT_FOUND);
        user.update(userInfoRequestDto);
    }
}
