package com.taskmanager.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.taskmanager.entity.User;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.respository.UserRepository;
@Component
public class UserUtil {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordUtil passwordUtil;
	
	private void validateUsernameAvailability(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new UsernameTakenException("El nombre de usuario ya está en uso.");
        }
    }

    private void validateEmailAvailability(String email) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new EmailInUseException("Hay una cuenta con esa dirección de correo electrónico: " + email);
        }
    }
	public void validateUserRegistration(String username, String email, String password) {
	    validateUsernameAvailability(username);
	    validateEmailAvailability(email);
	    passwordUtil.validatePasswordStrength(password);
	}
	public User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByUsername(authentication.getName());
		return user;
	}

}
