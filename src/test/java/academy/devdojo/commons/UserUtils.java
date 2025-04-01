package academy.devdojo.commons;

import academy.devdojo.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtils {
    public List<User> newUserList() {
        var user1 = User.builder()
                .id(1L)
                .firstName("Fulano")
                .lastName("Santos")
                .email("fulano@email.com")
                .roles("USER")
                .password("{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK")
                .build();
        var user2 = User.builder()
                .id(2L)
                .firstName("Ciclano")
                .lastName("Silva")
                .email("ciclano@email.com")
                .roles("USER")
                .password("{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK")
                .build();
        var user3 = User.builder()
                .id(3L)
                .firstName("Beltrano")
                .lastName("Almeida")
                .email("beltrano@email.com")
                .roles("USER")
                .password("{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK")
                .build();

        return new ArrayList<>(List.of(user1, user2, user3));

    }

    public User newUserToSave() {
        return User.builder()
                .firstName("Leonardo")
                .lastName("Santos")
                .email("leo@email.com")
                .roles("USER")
                .password("{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK")
                .build();
    }

    public User newUserSaved() {
        return User.builder()
                .id(99L)
                .firstName("Leonardo")
                .lastName("Santos")
                .email("leo@email.com")
                .roles("USER")
                .password("{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK")
                .build();
    }

}
