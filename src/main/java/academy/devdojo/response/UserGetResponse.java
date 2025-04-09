package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserGetResponse {

  @Schema(description = "User's id", example = "1")
  private Long id;
  @Schema(description = "User's first name", example = "Fulano")
  private String firstName;
  @Schema(description = "User's last name", example = "Santos")
  private String lastName;
  @Schema(description = "User's email. Must be unique", example = "fulano@gmail.com")
  private String email;
}
