package s3Storage.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s3Storage.dataobject.request.CreateUserRequest;
import s3Storage.dataobject.response.CreateUserResponse;
import s3Storage.entity.User;
import s3Storage.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/createUser")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            String username = request.getUsername();
            String name = request.getName();
            String password = request.getPassword();

            User user = userService.createUser(name, username, password);
            CreateUserResponse response = new CreateUserResponse(HttpStatus.OK.value(), null, user.getName(), user.getUsername());
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            LOGGER.error("Error while trying to create user : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/loginUser")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        User user = userService.findByUsername(username);
        if(user != null && user.checkPassword(password)) {
            String sessionId = userService.createSession(user);
            Cookie cookie = new Cookie("session_id", sessionId);
            response.addCookie(cookie);
            return ResponseEntity.ok("Login successful");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }

    @GetMapping("/printUserDetails")
    public ResponseEntity<CreateUserResponse> printUserDetails(@CookieValue(value = "session_id") String sessionId) {
        if(sessionId != null && sessionId.length() > 0) {
            User user = userService.getUserBySessionId(sessionId);
            if(user != null) {
                CreateUserResponse response = new CreateUserResponse(HttpStatus.OK.value(), null, user.getName(), user.getUsername());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
