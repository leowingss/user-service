package academy.devdojo.controller;

import academy.devdojo.config.RestAssuredConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestAssuredConfig.class)
@Sql(value = "sql/user/init_one_login_regular_user.sql")
@AutoConfigureWireMock(port = 0, files = "classpath:/wiremock/brasil-api/cep", stubs = "classpath:/wiremock/brasil-api/mappings")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BrasilApiControllerIT {
}
