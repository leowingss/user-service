package academy.devdojo.service;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;
    private List<User> usersList;

    @InjectMocks
    private UserUtils userUtils;

    @BeforeEach
    void init() {
        usersList = userUtils.newUserList();
    }

    @Test
    @DisplayName("findAll returns a list with all users when argument is null")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(usersList);
        var users = service.findAll(null);
        Assertions.assertThat(users).isNotNull().hasSameElementsAs(usersList);
    }

    @Test
    @DisplayName("findAll returns list with found object when firstname exists")
    @Order(2)
    void findByName_ReturnsFoundUser_WhenFirstNameIsFound() {
        var user = usersList.getFirst();
        var expectedUsersFound = Collections.singletonList(user);
        BDDMockito.when(repository.findByFirstNameIgnoreCase(user.getFirstName())).thenReturn(expectedUsersFound);
        var usersFound = service.findAll(user.getFirstName());
        Assertions.assertThat(usersFound).containsAll(expectedUsersFound);
    }

    @Test
    @DisplayName("findAll returns empty list when firstname is not found")
    @Order(3)
    void findByName_ReturnsEmptyList_WhenFirstNameIsNotFound() {
        var firstName = "not-found";
        BDDMockito.when(repository.findByFirstNameIgnoreCase(firstName)).thenReturn(Collections.emptyList());
        var users = service.findAll(firstName);
        Assertions.assertThat(users).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById returns a user with given id")
    @Order(4)
    void findById_ReturnsUserById_WhenSuccessfull() {
        var expectedUser = usersList.getFirst();
        BDDMockito.when(repository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

        var users = service.findByIdOrThrowNotFound(expectedUser.getId());
        Assertions.assertThat(users).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("findById throws ResponseStatusException when user is notFound")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenUserIsNotFound() {
        var expectedUser = usersList.getFirst();
        BDDMockito.when(repository.findById(expectedUser.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByIdOrThrowNotFound(expectedUser.getId()))
                .isInstanceOf(ResponseStatusException.class);

    }

    @Test
    @DisplayName("save creates an user")
    @Order(6)
    void save_CreatesUser_WhenSuccessfull() {
        var userToSaved = userUtils.newUserToSave();

        BDDMockito.when(repository.save(userToSaved)).thenReturn(userToSaved);

        var savedUser = service.save(userToSaved);

        Assertions.assertThat(savedUser).isEqualTo(userToSaved).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete removes an user")
    @Order(7)
    void delete_RemoveUser_WhenSuccesfull() {

        var userToDelete = usersList.getFirst();
        BDDMockito.when(repository.findById(userToDelete.getId())).thenReturn(Optional.of(userToDelete));
        BDDMockito.doNothing().when(repository).delete(userToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(userToDelete.getId()));

    }

    @Test
    @DisplayName("delete throws ResponseStatusException when user is notFound")
    @Order(8)
    void delete_ThrowsReponseStatusException_WhenUserIsNotFound() {

        var userToDelete = usersList.getFirst();
        BDDMockito.when(repository.findById(userToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(userToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);

    }

    @Test
    @DisplayName("update updates an user")
    @Order(9)
    void update_UpdatesUser_WhenSuccessfull() {

        var userToUpdate = usersList.getFirst();
        userToUpdate.setFirstName("Thiago");

        BDDMockito.when(repository.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
        BDDMockito.when(repository.save(userToUpdate)).thenReturn(userToUpdate);

        service.update(userToUpdate);


        Assertions.assertThatNoException().isThrownBy(() -> service.update(userToUpdate));

    }

    @Test
    @DisplayName("update throws ResponseStatusException when user is notFound")
    @Order(10)
    void update_ThrowsResponseStatusException_WhenUserIsNotFound() {

        var userToUpdate = usersList.getFirst();
        userToUpdate.setFirstName("Thiago");

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());


        Assertions.assertThatException()
                .isThrownBy(() -> service.update(userToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }


}