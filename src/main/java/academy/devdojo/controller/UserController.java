package academy.devdojo.controller;

import academy.devdojo.domain.User;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User API", description = "User related endpoints")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserGetResponse>> findAll(@RequestParam(required = false) String firstName) {
        log.debug("Request received to a list all users, param first name '{}'", firstName);

        var users = service.findAll(firstName);

        var userGetResponse = mapper.toUserGetResponseList(users);

        return ResponseEntity.ok(userGetResponse);

    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<UserGetResponse>> findAllPaginated(Pageable pageable) {
        log.debug("Request received to a list all users paginated");

        var pageUserGetResponse = service.findAllPaginated(pageable).map(mapper::toUserGetResponse);
        return ResponseEntity.ok(pageUserGetResponse);

    }


    @GetMapping("{id}")
    public ResponseEntity<UserGetResponse> findById(@PathVariable Long id) {

        var user = service.findByIdOrThrowNotFound(id);
        var userGetResponse = mapper.toUserGetResponse(user);

        return ResponseEntity.ok(userGetResponse);

    }

    @PostMapping()
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
