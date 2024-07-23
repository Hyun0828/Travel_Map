package travel.travel.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import travel.travel.domain.CommonUser;
import travel.travel.repository.CommonUserRepository;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final CommonUserRepository commonUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CommonUser commonUser = commonUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(commonUser.getEmail())
                .password(commonUser.getPassword())
                .roles(commonUser.getRole().name())
                .build();
    }
}
