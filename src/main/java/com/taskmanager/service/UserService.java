package com.taskmanager.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.config.AppConfig;
import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.entity.VerificationToken;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.respository.UserRepository;
import com.taskmanager.respository.VerificationTokenRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private EmailService emailService;
    
    public User registerNewUser(User newUser) {
        // Realiza las validaciones necesarias antes de registrar al nuevo usuario
        if (isUsernameTaken(newUser.getUsername())) {
            throw new UsernameTakenException("El nombre de usuario ya está en uso.");
        }

        String password = newUser.getPassword();

        // Verifica que la contraseña no sea nula o en blanco
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidPasswordException("La contraseña no es válida.");
        }

        if (isEmailAlreadyInUse(newUser.getEmail())) {
            throw new EmailInUseException("Hay una cuenta con esa dirección de correo electrónico: " + newUser.getEmail());
        }
        String encodedPassword = AppConfig.passwordEncoder().encode(password);
        newUser.setPassword(encodedPassword);
        Role userRole = roleRepository.findByName("ROLE_USER");
        newUser.setRole(userRole);
        userRepository.save(newUser);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(newUser);
        verificationTokenRepository.save(verificationToken);
        String verificationLink = "http://localhost/signup/verify?token=" + verificationToken.getToken();
        String emailBody = "Haz clic en el siguiente enlace para verificar tu cuenta: " + verificationLink;

        emailService.sendVerificationEmail(newUser.getEmail(), "Verificación de cuenta", emailBody);

        return userRepository.save(newUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Otros métodos de UserService...

    public boolean isUsernameTaken(String username) {
        User existingUser = userRepository.findByUsername(username);
        return existingUser != null;
    }
    public boolean isEmailAlreadyInUse(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent();
    }
    public boolean isPasswordValid(User user, String password) {
        return AppConfig.passwordEncoder().matches(password, user.getPassword());
    }
}
