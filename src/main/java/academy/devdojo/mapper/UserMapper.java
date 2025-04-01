package academy.devdojo.mapper;

import academy.devdojo.annotation.EncondedMapping;
import academy.devdojo.domain.User;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import org.aspectj.lang.annotation.After;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = PasswordEncoderMapper.class
)
public interface UserMapper {
    @Mapping(target = "roles", constant = "USER")
    @Mapping(target = "password", qualifiedBy = EncondedMapping.class)
    User toUser(UserPostRequest postRequest);

    @Mapping(target = "password", qualifiedBy = EncondedMapping.class)
    User toUser(UserPutRequest request);

    UserPostResponse toUserPostResponse(User user);

    UserGetResponse toUserGetResponse(User user);

    List<UserGetResponse> toUserGetResponseList(List<User> user);

    @Mapping(target = "password", source = "rawPassword", qualifiedBy = EncondedMapping.class)
    @Mapping(target = "roles", source = "savedUser.roles")
    @Mapping(target = "id", source = "userToUpdate.roles")
    @Mapping(target = "firstName", source = "userToUpdate.firstName")
    @Mapping(target = "lastName", source = "userToUpdate.lastName")
    @Mapping(target = "email", source = "userToUpdate.email")
    User toUserWithPasswordAndRoles(User userToUpdate, String rawPassword, User savedUser);

    @AfterMapping
    default void setPasswordIfNull(@MappingTarget User user, String rawPassword, User savedUser) {
        if (rawPassword == null) {
            user.setPassword(savedUser.getPassword());
        }
    }

}
