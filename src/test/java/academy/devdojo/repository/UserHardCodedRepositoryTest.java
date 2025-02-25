package academy.devdojo.repository;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserHardCodedRepositoryTest {

    @InjectMocks
    private UserHardCodedRepository repository;
    @Mock
    private UserData userData;
    private List<User> userList;
    @InjectMocks
    private UserUtils userUtils;

    @BeforeEach
    void init() {
        userList = userUtils.newUserList();
    }

    @Test
    @DisplayName("findAll returns a list with all user")
    @Order(1)
    void findAll_ReturnsAllUser_WhenSuccessfull() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);
        var users = repository.findAll();
        assertThat(users).isNotNull().hasSameElementsAs(userList);
    }

    @Test
    @DisplayName("findById returns a user with given id")
    @Order(2)
    void findById_ReturnsUserById_WhenSuccessfull() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);
        var expectedUser = userList.getFirst();
        var users = repository.findById(expectedUser.getId());
        Assertions.assertThat(users).isPresent().contains(expectedUser);
    }

    @Test
    @DisplayName("findByFirstName returns empty list when firstName is null")
    @Order(3)
    void findByFirstName_ReturnsEmptyList_WhenFirstNameIsNull() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);
        var users = repository.findByFirstName(null);
        Assertions.assertThat(users).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findByFirstName returns list with found object when firstName exists")
    @Order(4)
    void findByFirstName_ReturnsFoundUser_WhenFirstNameIsFound() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);
        var expectedUser = userList.getFirst();
        var users = repository.findByFirstName(expectedUser.getFirstName());
        Assertions.assertThat(users).contains(expectedUser);
    }

    @Test
    @DisplayName("save creates an user")
    @Order(5)
    void save_CreatesUser_WhenSuccessfull() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);
        var userToSave = userUtils.newUserToSave();

        var user = repository.save(userToSave);

        Assertions.assertThat(user).isEqualTo(userToSave).hasNoNullFieldsOrProperties();
        var userSavedOptional = repository.findById(userToSave.getId());
        Assertions.assertThat(userSavedOptional).isPresent().contains(userToSave);

    }

    @Test
    @DisplayName("delete removes an user")
    @Order(6)
    void delete_RemoveUser_WhenSuccesfull() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);
        var userToDelete = userList.getFirst();
        repository.delete(userToDelete);

        var users = repository.findAll();

        Assertions.assertThat(users).isNotEmpty().doesNotContain(userToDelete);

    }

    @Test
    @DisplayName("update updates a user")
    @Order(7)
    void update_UpdatesUser_WhenSuccessfull() {
        BDDMockito.when(userData.getUsers()).thenReturn(userList);

        var userToUpdate = this.userList.getFirst();
        userToUpdate.setFirstName("Thiago");
        repository.update(userToUpdate);
        Assertions.assertThat(this.userList).contains(userToUpdate);

        var userUpdatedOptional = repository.findById(userToUpdate.getId());

        Assertions.assertThat(userUpdatedOptional).isPresent();
        Assertions.assertThat(userUpdatedOptional.get().getFirstName()).isEqualTo(userToUpdate.getFirstName());

    }






}