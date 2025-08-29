package com.vsoft.repository;

import com.vsoft.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest   // Boots only JPA components with H2 in-memory DB
class UserRepositoryTest {

    @Autowired
     UserRepository userRepository;

    @Test
    @DisplayName("Should save and find user by email")
    void testFindByEmail_Success() {
        // Arrange
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setPhone("1234567890");
        user.setDegree("B.Tech");

        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testFindByEmail_NotFound() {
        // Act
        Optional<User> found = userRepository.findByEmail("missing@example.com");

        // Assert
        assertThat(found).isEmpty();
    }
}
