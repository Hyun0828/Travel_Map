package travel.travel.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class UserInfo extends BaseEntity{

    @Column(unique = true)
    protected String email;

    protected String name;
    protected LocalDate birth;
    protected Character gender;
    protected Integer age;
    protected String location;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }
}
