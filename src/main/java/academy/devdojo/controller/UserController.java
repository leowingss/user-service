package academy.devdojo.controller;

import academy.devdojo.domain.User;
import academy.devdojo.exception.ApiError;
import academy.devdojo.exception.DefaultErrorMessage;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
@Tag(name = "User API", description = "User related endpoints")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users available in the system",
            responses = {
                    @ApiResponse(description = "List all users",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserGetResponse.class))
                            )
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetResponse>> findAll(@RequestParam(required = false) String firstName) {
        log.debug("Request received to a list all users, param first name '{}'", firstName);

        var users = service.findAll(firstName);

        var userGetResponse = mapper.toUserGetResponseList(users);

        return ResponseEntity.ok(userGetResponse);

    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<UserGetResponse>> findAllPaginated(@ParameterObject Pageable pageable) {
        log.debug("Request received to a list all users paginated");

        var pageUserGetResponse = service.findAllPaginated(pageable).map(mapper::toUserGetResponse);
        return ResponseEntity.ok(pageUserGetResponse);

    }


    @GetMapping("{id}")
    @Operation(summary = "Get user by id",
            responses = {
                    @ApiResponse(description = "Get user by its id",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserGetResponse.class)
                            )
                    ),
                    @ApiResponse(description = "User not found",
                            responseCode = "404",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    )
            }
    )
    public ResponseEntity<UserGetResponse> findById(@PathVariable Long id) {

        var user = service.findByIdOrThrowNotFound(id);
        var userGetResponse = mapper.toUserGetResponse(user);

        return ResponseEntity.ok(userGetResponse);

    }

    @PostMapping()
    @Operation(summary = "Create user",
            responses = {
                    @ApiResponse(description = "Save user in the database",
                            responseCode = "201",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserPostResponse.class)
                            )
                    ),
                    @ApiResponse(description = "User not found",
                            responseCode = "400",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest userPostRequest) {

        log.debug("Request to save user: {}", userPostRequest);

        var user = mapper.toUser(userPostRequest);
        var userSaved = service.save(user);
        var userPostResponse = mapper.toUserPostResponse(userSaved);


        return ResponseEntity.status(HttpStatus.CREATED).body(userPostResponse);

    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();

    }

    @PutMapping()
    public ResponseEntity<Void> update(@RequestBody @Valid UserPutRequest request) {

        var userToUpdate = mapper.toUser(request);
        service.update(userToUpdate);

        return ResponseEntity.noContent().build();

    }


}
