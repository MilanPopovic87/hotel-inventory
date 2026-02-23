package com.milanpopovic.hotelinventory.service;

import com.milanpopovic.hotelinventory.entity.User;
import com.milanpopovic.hotelinventory.repository.BookingRepository;
import com.milanpopovic.hotelinventory.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BookingRepository bookingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookingRepository = bookingRepository;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @PostConstruct
    public void initAdminUser() {
        userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPassword(passwordEncoder.encode("admin123")); // demo
                    admin.setRole("ADMIN");
                    return userRepository.save(admin);
                });
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    public User getUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    public User saveUser(User user) {

        // 1) Check if username is already taken by another user
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            // Another user with same username exists → conflict
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User name already exists"
            );
        }

        // 2) PASSWORD HANDLING
        // - Create: password is required
        // - Update: password optional
        //   - If password blank => keep old password
        //   - If password provided => encode it (unless already encoded)

        if (user.getPassword() != null && !user.getPassword().isBlank()) {

            // If frontend sends plain password → encode it
            // If frontend accidentally sends already encoded BCrypt hash → don't encode again
            // BCrypt hashes usually start with "$2a$", "$2b$", "$2y$"
            if (!user.getPassword().startsWith("$2")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

        } else {

            // Password is missing/blank

            if (user.getId() == null) {
                // Creating a new user without a password is not allowed
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Password is required"
                );
            }

            // Updating an existing user without password → keep the old password from DB
            User dbUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    ));

            user.setPassword(dbUser.getPassword());
        }

        // 3) Save user (create or update)
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        if (bookingRepository.existsByUserId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User has bookings and cannot be deleted"
            );
        }

        userRepository.delete(user);
    }

}
