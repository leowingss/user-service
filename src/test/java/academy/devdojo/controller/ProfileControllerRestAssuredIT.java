package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.config.IntegrationTestConfig;
import academy.devdojo.config.RestAssuredConfig;
import academy.devdojo.config.TestcontainersConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
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
public class ProfileControllerRestAssuredIT extends IntegrationTestConfig {

    private static final String URL = "/v1/profiles";

    @Autowired
    private ProfileUtils profileUtils;

    @Autowired
    private FileUtils fileUtils;
    @LocalServerPort
    private int port;
    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;

    @BeforeEach
    void setUrl() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Test
    @DisplayName("GET v1/profiles returns a list with all profiles")
    @Order(1)
    @Sql(value = "/sql/profile/init_two_profile.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/profile/clean_profiles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsAllProfiles_WhenArgumentIsNull() {
        var response = fileUtils.readResourceFile("profile/get-profiles-200.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();

    }

    @Test
    @DisplayName("GET v1/profiles returns empty list when nothing is not found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNothingIsNotFound() throws Exception {
        var response = fileUtils.readResourceFile("profile/get-profiles-empty-list-200.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/profiles creates an profile")
    @Order(3)
    void save_CreatesProfile_WhenSuccessfull() {
        var request = fileUtils.readResourceFile("profile/post-request-profile-200.json");
        var expectedResponse = fileUtils.readResourceFile("profile/post-response-profile-201.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
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

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSource")
    @DisplayName("POST v1/profiles returns bad request when field are invalid")
    @Order(4)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFile, String responseFile) throws Exception {
        var request = fileUtils.readResourceFile("profile/%s".formatted(requestFile));
        var expectedResponse = fileUtils.readResourceFile("profile/%s".formatted(responseFile));


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


    private static Stream<Arguments> postProfileBadRequestSource() {

        return Stream.of(
                Arguments.of("post-request-profile-empty-fields-400.json", "post-response-profile-empty-fields-400.json"),
                Arguments.of("post-request-profile-blank-fields-400.json", "post-response-profile-blank-fields-400.json")
        );
    }


}
