package academy.devdojo.repository;

import academy.devdojo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByFirstNameIgnoreCase(String firstName);

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIdNot(String email, Long id);

}
