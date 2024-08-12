package travel.travel.mapper;

import travel.travel.domain.CommonUser;
import travel.travel.domain.Role;
import travel.travel.dto.user.CommonUserRequest;

public class CommonUserMapper {

    public static CommonUser toCommonUserFromCommonUserSignUpRequestDto(CommonUserRequest.CommonUserSignUpRequestDTO commonUserSignUpRequestDto) {

        return CommonUser.builder()
                .email(commonUserSignUpRequestDto.getEmail())
                .password(commonUserSignUpRequestDto.getPassword())
                .name(commonUserSignUpRequestDto.getName())
                .birth(commonUserSignUpRequestDto.getBirth())
                .gender(commonUserSignUpRequestDto.getGender())
                .age(commonUserSignUpRequestDto.getAge())
                .location(commonUserSignUpRequestDto.getLocation())
                .role(Role.USER)
                .build();
    }
}
