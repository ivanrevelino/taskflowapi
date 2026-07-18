package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.util.Generator;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    @DisplayName("Should save user successfully")
    void checkIfCreateSaveUserWhenSuccessful() {
        User userToBeSaved = Generator.generateUser();
        User saved = repository.save(userToBeSaved);

        Assertions.assertThat(saved.getId()).isNotNull();
        Assertions.assertThat(saved)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(userToBeSaved);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully(){
        User user = Generator.generateUser();
        User saved = repository.save(user);

        repository.delete(saved);

        Optional<User> userById = repository.findById(saved.getId());

        Assertions.assertThat(userById).isEmpty();
    }

    @Test
    @DisplayName("Find by Id should return right user")
    void findByIdShouldReturnsRightUser() {
        User userToBeSaved = Generator.generateUser();
        User saved = repository.save(userToBeSaved);

        User userById = repository.findById(saved.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        Assertions.assertThat(userById).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    @DisplayName("Find by id should not return user when id does not exists")
    void findByIdShouldNotReturnUserWhenIdDoesNotExist() {
        long nonExistentId = 12L;

        Optional<User> userById = repository.findById(nonExistentId);
        Assertions.assertThat(userById).isEmpty();
    }

    @Test
    @DisplayName("Should find user by login")
    void shouldFindUserByLogin() {
        User user = Generator.generateUser();
        repository.save(user);

        Optional<UserDetails> found = repository.findByUsername(user.getUsername());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Should return empty when login does not exist")
    void shouldReturnEmptyWhenLoginDoesNotExist() {
        Optional<UserDetails> found = repository.findByUsername("login.que.nao.existe@email.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should exist by id")
    void shouldExistById() {
        User saved = repository.save(Generator.generateUser());
        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }
}
