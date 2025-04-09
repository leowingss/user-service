package academy.devdojo.mapper;

import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.response.UserProfileGetResponse;
import academy.devdojo.response.UserProfileUserGetResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {

  List<UserProfileGetResponse> toUserProfileGetResponse(List<UserProfile> userProfiles);


  List<UserProfileUserGetResponse> toUserProfileUserGetResponseList(List<User> users);

}
