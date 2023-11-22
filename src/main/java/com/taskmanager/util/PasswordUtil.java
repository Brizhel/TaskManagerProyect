package com.taskmanager.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.taskmanager.entity.User;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.PasswordMismatchException;
import com.taskmanager.exception.PasswordNotChangedException;
import com.taskmanager.exception.WeakPasswordException;
@Component
public class PasswordUtil {
	@Autowired
	private PasswordEncoder passwordEncoder;

    // Otros métodos y atributos según sea necesario

    public String encryptPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }

        return passwordEncoder.encode(password);
    }

    public void validatePasswordStrength(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidPasswordException("La contraseña no es válida.");
        }

        // Agrega tu lógica de validación de fortaleza de contraseña aquí.
        // Puedes verificar la longitud, complejidad, etc.
        if (password.length() < 8) {
            throw new WeakPasswordException("La contraseña es débil. Debe tener al menos 8 caracteres.");
        }
        // Agrega más validaciones según sea necesario.
    }   
    public void validateOldPassword(String oldPassword, User user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordMismatchException("La antigua contraseña no es válida");
        }
    }
    public void validateNewPassword(String newPassword, User user) {
		if (passwordEncoder.matches(newPassword, user.getPassword())) {
			throw new PasswordNotChangedException("La nueva contraseña debe ser diferente de la contraseña actual");
		}
    }
}