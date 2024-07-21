package travel.travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String password;

    // OAuth2 로그인 시 JWT 인증을 위한 email
    private String oauth_email;

    // 시큐리티 필터를 위한 Role
    // OAuth2 로그인 시, 개인정보를 입력하지 않았다면 GUEST, 했다면 USER
//    @Enumerated(EnumType.STRING)
//    private Role role;

//     KAKAO, NAVER, GOOGLE
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

//     로그인한 소셜 타입의 식별자 값 (자체 로그인의 경우 null)
    private String socialId;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private UserImage userImage;

    public void setUserImage(UserImage userImage) {
        this.userImage = userImage;
    }

    // 유저 권한 설정 메소드
//    public void authorizeUser() {
//        this.role = Role.USER;
//    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // 비밀번호 확인 메소드
    public boolean checkPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
