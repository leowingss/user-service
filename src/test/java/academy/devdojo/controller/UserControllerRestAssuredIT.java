package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.config.IntegrationTestConfig;
import academy.devdojo.config.RestAssuredConfig;
import academy.devdojo.config.TestcontainersConfiguration;
import academy.devdojo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestAssuredConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestcontainersConfiguration.class)
public class UserControllerRestAssuredIT extends IntegrationTestConfig {

    private static final String URL = "/v1/users";

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private UserRepository repository;

    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;
    @Autowired
    @Qualifier(value = "requestSpecificationAdminUser")
    private RequestSpecification requestSpecificationAdminUser;

    @BeforeEach
    void setUrl() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Test
    @DisplayName("GET v1/users returns a list with all users when argument is null")
    @Order(1)
    @Sql(value = "/sql/user/init_three_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsAllUsers_WhenArgumentIsNull() {
        RestAssured.requestSpecification = requestSpecificationAdminUser;

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
        RestAssured.requestSpecification = requestSpecificationAdminUser;

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
        RestAssured.requestSpecification = requestSpecificationAdminUser;


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

    @Test
    @DisplayName("GET v1/users/99 throws NotFound 404 when user is notFound")
    @Sql(value = "/sql/user/init_three_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(5)
    void findById_ThrowsNotFound_WhenUserIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/get-user-by-id-404.json");

        var id = 99L;

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", id)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();

    }

    @Test
    @DisplayName("POST v1/users creates an user")
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(6)
    void save_CreatesUser_WhenSuccessfull() throws Exception {
        var request = fileUtils.readResourceFile("user/post-request-user-200.json");
        var expectedResponse = fileUtils.readResourceFile("user/post-response-user-201.json");


        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .body(request)
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("id")
                .asNumber()
                .isPositive();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("PUT v1/users update updates a user")
    @Sql(value = "/sql/user/init_one_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(7)
    void update_UpdatesUser_WhenSuccessfull() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-200.json");
        var users = repository.findByFirstNameIgnoreCase("Leonardo");

        Assertions.assertThat(users).hasSize(1);
        request = request.replace("1", users.getFirst().getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .body(request)
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/users throws NotFound when user is notFound")
    @Order(8)
    void update_ThrowsNotFound_WhenUserIsNotFound() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-404.json");
        var expectedResponse = fileUtils.readResourceFile("user/put-user-by-id-404.json");


        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .body(request)
                .put(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();

    }

    @Test
    @DisplayName("DELETE v1/users/1 removes an user")
    @Sql(value = "/sql/user/init_one_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/clean_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(9)
    void delete_RemoveUser_WhenSuccesfull() throws Exception {
        var id = repository.findAll().getFirst().getId();
        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", id)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

    }

    @Test
    @DisplayName("DELETE v1/users/99 throws NotFound when user is notFound")
    @Order(10)
    void delete_ThrowsReponseStatusException_WhenUserIsNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/delete-user-by-id-404.json");
        var id = 99;

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", id)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();

    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSource")
    @DisplayName("POST v1/users returns bad request when field are invalid")
    @Order(11)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(requestFile));
        var expectedResponse = fileUtils.readResourceFile("user/%s".formatted(responseFile));

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);

    }


    @ParameterizedTest
    @MethodSource("putUserBadRequestSource")
    @DisplayName("PUT v1/users returns bad request when field are invalid")
    @Order(12)
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(requestFile));
        var expectedResponse = fileUtils.readResourceFile("user/%s".formatted(responseFile));

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);

    }


    private static Stream<Arguments> postUserBadRequestSource() {

        return Stream.of(
                Arguments.of("post-request-user-empty-fields-400.json", "post-response-user-empty-fields-400.json"),
                Arguments.of("post-request-user-blank-fields-400.json", "post-response-user-blank-fields-400.json"),
                Arguments.of("post-request-user-invalid-email-400.json", "post-response-user-invalid-email-400.json")
        );
    }


    private static Stream<Arguments> putUserBadRequestSource() {

        return Stream.of(
                Arguments.of("put-request-user-empty-fields-400.json", "put-response-user-empty-fields-400.json"),
                Arguments.of("put-request-user-blank-fields-400.json", "put-response-user-blank-fields-400.json"),
                Arguments.of("put-request-user-invalid-email-400.json", "put-response-user-invalid-email-400.json")
        );
    }

}
