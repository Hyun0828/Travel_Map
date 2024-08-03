package travel.travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import travel.travel.dto.story.StoryUpdateRequestDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    private String address;

    private LocalDate date;

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<StoryImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void addImage(StoryImage image) {
        if (!images.contains(image))
            images.add(image);
        image.setStory(this);
    }

    public void removeImage(StoryImage image) {
        this.images.remove(image);
        image.setStory(null);
    }

    public void update(StoryUpdateRequestDto storyUpdateRequestDto) {
        this.title = storyUpdateRequestDto.getTitle();
        this.content = storyUpdateRequestDto.getContent();
        this.place = storyUpdateRequestDto.getPlace();
        this.address = storyUpdateRequestDto.getAddress();
        this.date = storyUpdateRequestDto.getDate().toLocalDate();
    }
}
