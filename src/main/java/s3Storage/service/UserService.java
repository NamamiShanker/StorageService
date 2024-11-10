package s3Storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import s3Storage.entity.User;
import s3Storage.repository.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private Map<String, User> sessions = new ConcurrentHashMap<>();

    public User createUser(String name, String username, String password) {
        User user = new User(name, username);
        // TODO: Add hashing and salting
        user.setPassword(password);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public User getUserBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }
}
