package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.config.IntegrationTestConfig;
import academy.devdojo.config.TestcontainersConfiguration;
import academy.devdojo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestcontainersConfiguration.class)
public class UserControllerRestAssuredIT extends IntegrationTestConfig {

    private static final String URL = "/v1/users";

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FileUtils fileUtils;
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET v1/users returns a list with all users when argument is null")
    @Order(1)
    @Sql(value = "/sql/user/init_three_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsAllUsers_WhenArgumentIsNull() {
        var expectedResponse = fileUtils.readResourceFile("user/get-user-null-first-name-200.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();


        JsonAssertions.assertThatJson(response)
                .and(users -> {
                    users.node("[0].id").asNumber().isPositive();
                    users.node("[1].id").asNumber().isPositive();
                    users.node("[2].id").asNumber().isPositive();
                });

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id") //todo mundo que for id, pois é um array
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("GET v1/users?firstName=Fulano returns list with found object when first name exists")
    @Sql(value = "/sql/user/init_three_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void findAll_ReturnsFoundUser_WhenFirstNameIsFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/get-user-fulano-first-name-200.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("firstName", "Fulano")
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("GET v1/users?firstName=x findAll returns empty list when firstname is not found")
    @Sql(value = "/sql/user/init_three_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/get-user-x-first-name-200.json");
        var firstName = "x";

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("firstName", firstName)
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("GET v1/users/1 returns a user with given id")
    @Sql(value = "/sql/user/init_three_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    void findById_ReturnsUserById_WhenSuccessfull() throws Exception {

        var expectedResponse = fileUtils.readResourceFile("user/get-user-by-id-200.json");
        var users = repository.findByFirstNameIgnoreCase("Fulano");

        Assertions.assertThat(users).hasSize(1);


        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", users.getFirst().getId())
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);

    }

//    @Test
//    @DisplayName("GET v1/users/99 throws NotFound 404 when user is notFound")
//    @Order(5)
//    void findById_ThrowsNotFound_WhenUserIsNotFound() throws Exception {
//        var id = 99L;
//        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//
//    }

//    @Test
//    @DisplayName("POST v1/users creates an user")
//    @Order(6)
//    void save_CreatesUser_WhenSuccessfull() throws Exception {
//        var request = fileUtils.readResourceFile("user/post-request-user-200.json");
//        var response = fileUtils.readResourceFile("user/post-response-user-201.json");
//
//        var userSaved = userUtils.newUserSaved();
//
//        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(userSaved);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post(URL)
//                        .content(request)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.content().json(response));
//    }
//
//    @Test
//    @DisplayName("PUT v1/users update updates a user")
//    @Order(7)
//    void update_UpdatesUser_WhenSuccessfull() throws Exception {
//        var request = fileUtils.readResourceFile("user/put-request-user-200.json");
//        var id = 1L;
//        var foundUser = usersList.stream().filter(user -> user.getId().equals(id)).findFirst();
//
//        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put(URL)
//                        .content(request)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//
//    }
//
//    @Test
//    @DisplayName("PUT v1/users throws NotFound when user is notFound")
//    @Order(8)
//    void update_ThrowsNotFound_WhenUserIsNotFound() throws Exception {
//        var request = fileUtils.readResourceFile("user/put-request-user-404.json");
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put(URL)
//                        .content(request)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//
//    }
//
//    @Test
//    @DisplayName("DELETE v1/users/1 removes an user")
//    @Order(9)
//    void delete_RemoveUser_WhenSuccesfull() throws Exception {
//        var id = usersList.getFirst().getId();
//        var foundUser = usersList.stream().filter(user -> user.getId().equals(id)).findFirst();
//        BDDMockito.when(repository.findById(id)).thenReturn(foundUser);
//
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete(URL + "/{id}", id)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//    }
//
//    @Test
//    @DisplayName("DELETE v1/users/99 throws NotFound when user is notFound")
//    @Order(10)
//    void delete_ThrowsReponseStatusException_WhenUserIsNotFound() throws Exception {
//
//        var id = 99;
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete(URL + "/{id}", id)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("postUserBadRequestSource")
//    @DisplayName("POST v1/users returns bad request when field are invalid")
//    @Order(11)
//    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
//        var request = fileUtils.readResourceFile("user/%s".formatted(fileName));
//
//        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post(URL)
//                        .content(request)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andReturn();
//
//        var resolvedException = mvcResult.getResolvedException();
//        org.assertj.core.api.Assertions.assertThat(resolvedException).isNotNull();
//
//        org.assertj.core.api.Assertions.assertThat(resolvedException.getMessage())
//                .contains(errors);
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("putUserBadRequestSource")
//    @DisplayName("PUT v1/users returns bad request when field are invalid")
//    @Order(12)
//    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
//        var request = fileUtils.readResourceFile("user/%s".formatted(fileName));
//
//        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
//                        .put(URL)
//                        .content(request)
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andReturn();
//
//        var resolvedException = mvcResult.getResolvedException();
//        org.assertj.core.api.Assertions.assertThat(resolvedException).isNotNull();
//
//        Assertions.assertThat(resolvedException.getMessage())
//                .contains(errors);
//
//    }
//
//
//    private static Stream<Arguments> postUserBadRequestSource() {
//
//
//        var allRequiredErrors = allRequiredErrors();
//        var emailInvalidError = invalidEmailErrors();
//
//        return Stream.of(
//                Arguments.of("post-request-user-empty-fields-400.json", allRequiredErrors),
//                Arguments.of("post-request-user-blank-fields-400.json", allRequiredErrors),
//                Arguments.of("post-request-user-invalid-email-400.json", emailInvalidError)
//        );
//    }
//
//
//    private static Stream<Arguments> putUserBadRequestSource() {
//
//        var allRequiredErrors = allRequiredErrors();
//        allRequiredErrors.add("The field 'id' cannot be null");
//        var emailInvalidError = invalidEmailErrors();
//
//        return Stream.of(
//                Arguments.of("put-request-user-empty-fields-400.json", allRequiredErrors),
//                Arguments.of("put-request-user-blank-fields-400.json", allRequiredErrors),
//                Arguments.of("put-request-user-invalid-email-400.json", emailInvalidError)
//        );
//    }
//
//    private static List<String> invalidEmailErrors() {
//        var emailInvalidError = "Email is not valid";
//        return List.of(emailInvalidError);
//    }
//
//    private static List<String> allRequiredErrors() {
//
//        var firstNameRequiredError = "The field 'firstName' is required";
//        var lastNameRequiredError = "The field 'lastName' is required";
//        var emailRequiredError = "The field 'email' is required";
//
//        return new ArrayList<>(List.of(firstNameRequiredError, lastNameRequiredError, emailRequiredError));
//    }


}
