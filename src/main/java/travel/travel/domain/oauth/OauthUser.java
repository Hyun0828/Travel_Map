package travel.travel.domain.oauth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import travel.travel.domain.User;

import static lombok.AccessLevel.PROTECTED;

@Entity
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("O")
@Table(name = "oauth_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "oauth_id_unique",
                        columnNames = {
                                "oauth_server_id",
                                "oauth_server"
                        }
                ),
        }
)
@PrimaryKeyJoinColumn(name = "oauth_user_id")
public class OauthUser extends User {

    @Embedded
    private OauthId oauthId;
    private String profileImageUrl;

}