package travel.travel.mapper;

import travel.travel.domain.CommonUser;
import travel.travel.domain.User;
import travel.travel.domain.oauth.OauthUser;
import travel.travel.dto.user.UserInfoResponseDto;

public class UserMapper {

    public static UserInfoResponseDto toUserInfoResponseDto(User user) {

        String dtype = null;
        if (user.getClass() == CommonUser.class)
            dtype = "C";
        else if (user.getClass() == OauthUser.class)
            dtype = "O";

        return UserInfoResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .birth(user.getBirth())
                .gender(user.getGender())
                .age(user.getAge())
                .location(user.getLocation())
                .dtype(dtype)
                .role(user.getRole())
                .imageUrl(user.getUserImage().getUrl())
                .build();
    }
}
