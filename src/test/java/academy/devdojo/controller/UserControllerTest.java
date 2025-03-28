package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserProfileRepository;
import academy.devdojo.repository.UserRepository;
import academy.devdojo.service.ProfileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(controllers = UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "academy.devdojo")
class UserControllerTest {

    private static final String URL = "/v1/users";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository repository;
    private List<User> usersList;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private UserUtils userUtils;
    @MockBean
    private ProfileService profileService;
    @MockBean
    private UserProfileRepository userProfileRepository;

    @BeforeEach
    void init() {
        usersList = userUtils.newUserList();
    }

    @Test
    @DisplayName("GET v1/users returns a list with all users when argument is null")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenArgumentIsNull() throws Exception {
        var response = fileUtils.readResourceFile("user/get-user-null-first-name-200.json");
        BDDMockito.when(repository.findAll()).thenReturn(usersList);
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/paginated returns a paginated list of users")
    @Order(1)
    void findAllPaginated_ReturnsPaginatedsers_WhenSuccessfull() throws Exception {
        var response = fileUtils.readResourceFile("user/get-user-paginated-200.json");

        var pageRequest = PageRequest.of(0, usersList.size());
        var pageUser = new PageImpl<>(usersList, pageRequest, 1);

        BDDMockito.when(repository.findAll(BDDMockito.any(Pageable.class))).thenReturn(pageUser);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/paginated"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }


    @Test
    @DisplayName("GET v1/users?firstName=Fulano returns list with found object when first name exists")
    @Order(2)
    void findAll_ReturnsFoundUser_WhenFirstNameIsFound() throws Exception {
        var response = fileUtils.readResourceFile("user/get-user-fulano-first-name-200.json");
        var firstName = "Fulano";
        var fulano = usersList.stream().filter(user -> user.getFirstName().equals(firstName)).findFirst().orElse(null);

        BDDMockito.when(repository.findByFirstNameIgnoreCase(firstName)).thenReturn(Collections.singletonList(fulano));

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users?firstName=x findAll returns empty list when firstname is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() throws Exception {
        var response = fileUtils.readResourceFile("user/get-user-x-first-name-200.json");
        var firstName = "x";
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/1 returns a user with given id")
    @Order(4)
    void findById_ReturnsUserById_WhenSuccessfull() throws Exception {

        var response = fileUtils.readResourceFile("user/get-user-by-id-200.json");
        var id = 1L;

        var foundUser = usersList.stream().filter(user -> user.getId().equals(id)).findFirst();
        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users/99 throws NotFound 404 when user is notFound")
    @Order(5)
    void findById_ThrowsNotFound_WhenUserIsNotFound() throws Exception {
        var id = 99L;
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("POST v1/users creates an user")
    @Order(6)
    void save_CreatesUser_WhenSuccessfull() throws Exception {
        var request = fileUtils.readResourceFile("user/post-request-user-200.json");
        var response = fileUtils.readResourceFile("user/post-response-user-201.json");

        var userSaved = userUtils.newUserSaved();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(userSaved);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("PUT v1/users update updates a user")
    @Order(7)
    void update_UpdatesUser_WhenSuccessfull() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-200.json");
        var id = 1L;
        var foundUser = usersList.stream().filter(user -> user.getId().equals(id)).findFirst();

        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("PUT v1/users throws NotFound when user is notFound")
    @Order(8)
    void update_ThrowsNotFound_WhenUserIsNotFound() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-404.json");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("DELETE v1/users/1 removes an user")
    @Order(9)
    void delete_RemoveUser_WhenSuccesfull() throws Exception {
        var id = usersList.getFirst().getId();
        var foundUser = usersList.stream().filter(user -> user.getId().equals(id)).findFirst();
        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);


        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + "/{id}", id)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/users/99 throws NotFound when user is notFound")
    @Order(10)
    void delete_ThrowsReponseStatusException_WhenUserIsNotFound() throws Exception {

        var id = 99;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + "/{id}", id)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSource")
    @DisplayName("POST v1/users returns bad request when field are invalid")
    @Order(11)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage())
                .contains(errors);

    }

    @ParameterizedTest
    @MethodSource("putUserBadRequestSource")
    @DisplayName("PUT v1/users returns bad request when field are invalid")
    @Order(12)
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();
        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage())
                .contains(errors);

    }


    private static Stream<Arguments> postUserBadRequestSource() {


        var allRequiredErrors = allRequiredErrors();
        var emailInvalidError = invalidEmailErrors();

        return Stream.of(
                Arguments.of("post-request-user-empty-fields-400.json", allRequiredErrors),
                Arguments.of("post-request-user-blank-fields-400.json", allRequiredErrors),
                Arguments.of("post-request-user-invalid-email-400.json", emailInvalidError)
        );
    }


    private static Stream<Arguments> putUserBadRequestSource() {

        var allRequiredErrors = allRequiredErrors();
        allRequiredErrors.add("The field 'id' cannot be null");
        var emailInvalidError = invalidEmailErrors();

        return Stream.of(
                Arguments.of("put-request-user-empty-fields-400.json", allRequiredErrors),
                Arguments.of("put-request-user-blank-fields-400.json", allRequiredErrors),
                Arguments.of("put-request-user-invalid-email-400.json", emailInvalidError)
        );
    }

    private static List<String> invalidEmailErrors() {
        var emailInvalidError = "Email is not valid";
        return List.of(emailInvalidError);
    }

    private static List<String> allRequiredErrors() {

        var firstNameRequiredError = "The field 'firstName' is required";
        var lastNameRequiredError = "The field 'lastName' is required";
        var emailRequiredError = "The field 'email' is required";

        return new ArrayList<>(List.of(firstNameRequiredError, lastNameRequiredError, emailRequiredError));
    }


}