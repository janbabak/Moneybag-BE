package com.babakjan.moneybag.repository;

import com.babakjan.moneybag.entity.Role;
import com.babakjan.moneybag.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByEmailFound() {
        // given
        String email = "some.user@gmail.com";
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email(email)
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByEmail(email);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailNotFound() {
        // when
        Optional<User> result = userRepository.findByEmail("randomuser@email.cz");

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}