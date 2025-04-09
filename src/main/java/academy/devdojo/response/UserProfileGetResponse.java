package academy.devdojo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserProfileGetResponse {

  //record aninhado
  public record User(Long id, String firstName) {

  }

  public record Profile(Long id, String name) {

  }

  private Long id;
  private User user;
  private Profile profile;
}
