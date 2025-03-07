package academy.devdojo.service;

import academy.devdojo.domain.User;
import academy.devdojo.exception.EmailAlreadyExistsException;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> findAll(String firstName) {
        return firstName == null ? repository.findAll() : repository.findByFirstNameIgnoreCase(firstName);
    }

    public Page<User> findAllPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }


    public User findByIdOrThrowNotFound(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    //@Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        assertEmailDoesNoExist(user.getEmail());
        return repository.save(user);
    }

    public void delete(Long id) {
        var user = findByIdOrThrowNotFound(id);
        repository.delete(user);
    }

    public void update(User userToUpdate) {
        assertUserExists(userToUpdate.getId());
        assertEmailDoesNoExist(userToUpdate.getEmail(), userToUpdate.getId());
        repository.save(userToUpdate);
    }

    public void assertUserExists(Long id) {
        findByIdOrThrowNotFound(id);
    }

    public void assertEmailDoesNoExist(String email) {
        repository.findByEmail(email)
                .ifPresent(this::throwEmailExistsException);
    }

    public void assertEmailDoesNoExist(String email, Long id) {
        repository.findByEmailAndIdNot(email, id)
                .ifPresent(this::throwEmailExistsException);
    }

    private void throwEmailExistsException(User user) {
        throw new EmailAlreadyExistsException("E-mail %s already exists".formatted(user.getEmail()));
    }


}
