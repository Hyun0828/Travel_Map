package travel.travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import travel.travel.dto.user.UserInfoRequestDto;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@SuperBuilder
public abstract class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    protected String email;

    protected String name;
    protected LocalDate birth;
    protected String gender;
    protected Integer age;
    protected String location;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserImage userImage;

    public void setUserImage(UserImage userImage) {
        this.userImage = userImage;
    }

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }

    // 개인 정보 업데이트 메소드
    public void update(UserInfoRequestDto userInfoRequestDto) {
        this.email = userInfoRequestDto.getEmail();
        this.name = userInfoRequestDto.getName();
        this.birth = userInfoRequestDto.getBirth();
        this.gender = userInfoRequestDto.getGender();
        this.age = userInfoRequestDto.getAge();
        this.location = userInfoRequestDto.getLocation();
        this.role = Role.USER;
    }
}
