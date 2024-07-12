package travel.travel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import travel.travel.domain.User;
import travel.travel.dto.UserIdResponseDto;
import travel.travel.dto.UserSignUpRequestDto;
import travel.travel.repository.UserRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserIdResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) throws Exception {
        if (userRepository.findByEmail(userSignUpRequestDto.getEmail()).isPresent())
            throw new Exception("Email already exists");

        User user = User.builder()
                .email(userSignUpRequestDto.getEmail())
                .password(userSignUpRequestDto.getPassword())
                .name(userSignUpRequestDto.getName())
                .profile(userSignUpRequestDto.getProfile())
                .birth(userSignUpRequestDto.getBirth())
                .gender(userSignUpRequestDto.getGender())
                .age(userSignUpRequestDto.getAge())
                .location(userSignUpRequestDto.getLocation())
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);

        return UserIdResponseDto.builder().userId(user.getId()).build();
    }
}
