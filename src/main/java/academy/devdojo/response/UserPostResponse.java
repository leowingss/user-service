package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserPostResponse {
    @Schema(description = "User's id", example = "1")
    private Long id;
}
