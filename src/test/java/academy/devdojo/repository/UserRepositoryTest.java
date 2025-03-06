package academy.devdojo.repository;

import academy.devdojo.commons.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserUtils.class)
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("save creates an user")
    @Order(1)
    void save_CreatesUser_WhenSuccessfull() {
        var userToSaved = userUtils.newUserToSave();
        var savedUser = repository.save(userToSaved);

        Assertions.assertThat(savedUser).hasNoNullFieldsOrProperties();
        Assertions.assertThat(savedUser.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findAll returns a list with all users ")
    @Order(2)
    @Sql("/sql/init_one_user.sql")
    void findAll_ReturnsAllUsers_WhenSuccessfull() {
        var users = repository.findAll();
        Assertions.assertThat(users).isNotEmpty();
    }


}