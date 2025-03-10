package academy.devdojo.commons;

import academy.devdojo.domain.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileUtils {
    public List<Profile> newProfileList() {
        var admin = Profile.builder().id(1L).name("Administrator").description("Admins everything").build();
        var manager = Profile.builder().id(2L).name("Manager").description("Manages users").build();

        return new ArrayList<>(List.of(admin, manager));

    }

    public Profile newProfileToSave() {
        return Profile.builder()
                .name("Regular User")
                .description("Regular user with regular permissions")
                .build();
    }
    public Profile newProfileSaved() {
        return Profile.builder()
                .id(99L)
                .name("Regular User")
                .description("Regular user with regular permissions")
                .build();
    }


}
