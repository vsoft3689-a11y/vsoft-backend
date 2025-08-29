package com.vsoft.repository;

import com.vsoft.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ✅ Use PostgreSQL, not H2
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and find user by email in PostgreSQL")
    void testFindByEmail_Success() {
        // Arrange
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setPhone("1234567890");
        user.setDegree("B.Tech");

        userRepository.saveAndFlush(user); // ✅ make sure data is persisted in PostgreSQL

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when email not found in PostgreSQL")
    void testFindByEmail_NotFound() {
        // Act
        Optional<User> found = userRepository.findByEmail("missing@example.com");

        // Assert
        assertThat(found).isEmpty();
    }
}
