package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Repositry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public String registerUser(User user) {
        // Validate email format
        if (!isValidEmail(user.getEmail())) {
            return "Invalid email format";
        }

        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return "Email already exists";
        }

        // Hash the password
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // Save the user
        userRepository.save(user);

        return "User registered successfully";
    }

    public String loginUser(String email, String password) {
        // Check if email exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            return "Invalid email";
        }

        // Verify the password
        User user = existingUser.get();
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return "Invalid password";
        }

        return "Login successful";
    }

    private boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }
}