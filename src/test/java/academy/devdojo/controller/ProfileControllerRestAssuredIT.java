package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.config.IntegrationTestConfig;
import academy.devdojo.config.TestcontainersConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET v1/profiles returns a list with all profiles")
    @Order(1)
    @Sql(value = "/sql/init_two_profile.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_profiles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

//    @Test
//    @DisplayName("GET v1/profiles returns empty list when nothing is not found")
//    @Order(2)
//    void findAll_ReturnsEmptyList_WhenNothingIsNotFound() throws Exception {
//        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {
//        };
//        var responseEntity = testRestTemplate.exchange(URL, HttpMethod.GET, null, typeReference);
//
//
//        Assertions.assertThat(responseEntity).isNotNull();
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Assertions.assertThat(responseEntity.getBody()).isNotNull().isEmpty();
//
//
//    }
//
//    @Test
//    @DisplayName("POST v1/profiles creates an profile")
//    @Order(3)
//    void save_CreatesProfile_WhenSuccessfull() throws Exception {
//        var request = fileUtils.readResourceFile("profile/post-request-profile-200.json");
//        var profileEntity = buildHttpEntity(request);
//
//        var responseEntity = testRestTemplate.exchange(URL, HttpMethod.POST, profileEntity, ProfilePostResponse.class);
//
//
//        Assertions.assertThat(responseEntity).isNotNull();
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        Assertions.assertThat(responseEntity.getBody()).isNotNull().hasNoNullFieldsOrProperties();
//
//
//    }
//
//    @ParameterizedTest
//    @MethodSource("postProfileBadRequestSource")
//    @DisplayName("POST v1/profiles returns bad request when field are invalid")
//    @Order(4)
//    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFile, String responseFile) throws Exception {
//        var request = fileUtils.readResourceFile("profile/%s".formatted(requestFile));
//        var expectedResponse = fileUtils.readResourceFile("profile/%s".formatted(responseFile));
//
//        var profileEntity = buildHttpEntity(request);
//        var responseEntity = testRestTemplate.exchange(URL, HttpMethod.POST, profileEntity, String.class);
//
//        Assertions.assertThat(responseEntity).isNotNull();
//        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        JsonAssertions.assertThatJson(responseEntity.getBody())
//                .whenIgnoringPaths("timestamp")
//                .isEqualTo(expectedResponse);
//    }
//
//
//    private static Stream<Arguments> postProfileBadRequestSource() {
//
//        return Stream.of(
//                Arguments.of("post-request-profile-empty-fields-400.json", "post-response-profile-empty-fields-400.json"),
//                Arguments.of("post-request-profile-blank-fields-400.json", "post-response-profile-blank-fields-400.json")
//        );
//    }
//
//
//    private static HttpEntity<String> buildHttpEntity(String request) {
//        var httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        return new HttpEntity<>(request, httpHeaders);
//    }


}
