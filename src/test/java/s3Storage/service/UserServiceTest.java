// UserServiceTest.java
package s3Storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import s3Storage.entity.User;
import s3Storage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize a test user
        testUser = new User("Test Name", "testuser");
        testUser.setPassword("password123");
    }

    @Test
    public void testCreateUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser("Test Name", "testuser", "password123");

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("Test Name", savedUser.getName());
        assertEquals("testuser", savedUser.getUsername());
        assertTrue(savedUser.checkPassword("password123"));

        assertEquals(testUser, createdUser);
    }

    @Test
    public void testFindByUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        // Act
        User foundUser = userService.findByUsername("testuser");

        // Assert
        verify(userRepository).findByUsername("testuser");
        assertEquals(testUser, foundUser);
    }

    @Test
    public void testCreateSession() {
        // Act
        String sessionId = userService.createSession(testUser);

        // Assert
        assertNotNull(sessionId);
        // Instead of accessing the private sessions map, we use the public method to verify
        User userFromSession = userService.getUserBySessionId(sessionId);
        assertEquals(testUser, userFromSession);
    }

    @Test
    public void testGetUserBySessionId() {
        // Arrange
        String sessionId = userService.createSession(testUser);

        // Act
        User user = userService.getUserBySessionId(sessionId);

        // Assert
        assertEquals(testUser, user);
    }

    @Test
    public void testGetUserByInvalidSessionId() {
        // Act
        User user = userService.getUserBySessionId("invalid-session-id");

        // Assert
        assertNull(user);
    }
}
