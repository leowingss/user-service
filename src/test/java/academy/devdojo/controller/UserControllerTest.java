package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.repository.UserData;
import academy.devdojo.repository.UserHardCodedRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "academy.devdojo")
class UserControllerTest {

    private static final String URL = "/v1/users";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserData userData;
    @SpyBean
    private UserHardCodedRepository repository;
    private List<User> usersList;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private UserUtils userUtils;

    @BeforeEach
    void init() {
        usersList = userUtils.newUserList();
    }

    @Test
    @DisplayName("GET v1/users returns a list with all users when argument is null")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenArgumentIsNull() throws Exception {
        BDDMockito.when(userData.getUsers()).thenReturn(usersList);
        var response = fileUtils.readResourceFile("user/get-user-null-first-name-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users?firstName=Fulano returns list with found object when first name exists")
    @Order(2)
    void findAll_ReturnsFoundUser_WhenFirstNameIsFound() throws Exception {
        BDDMockito.when(userData.getUsers()).thenReturn(usersList);
        var response = fileUtils.readResourceFile("user/get-user-fulano-first-name-200.json");
        var firstName = "Fulano";
        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("firstName", firstName))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/users?firstName=x findAll returns empty list when firstname is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() throws Exception {
        BDDMockito.when(userData.getUsers()).thenReturn(usersList);
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
        BDDMockito.when(userData.getUsers()).thenReturn(usersList);
        var response = fileUtils.readResourceFile("user/get-user-by-id-200.json");
        var id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName(" GET v1/users/99 throws ResponseStatusException 404 when user is notFound")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenProducerIsNotFound() throws Exception {
        BDDMockito.when(userData.getUsers()).thenReturn(usersList);
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

        var userToSave = userUtils.newUserToSave();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(userToSave);

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


}