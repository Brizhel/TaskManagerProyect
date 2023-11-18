package com.taskmanager.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taskmanager.config.AppConfig;
import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.entity.Token;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.InvalidLastNameException;
import com.taskmanager.exception.InvalidNameException;
import com.taskmanager.exception.PasswordMismatchException;
import com.taskmanager.exception.PasswordNotChangedException;
import com.taskmanager.exception.TokenExpiredException;
import com.taskmanager.exception.TokenNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.request.ChangePasswordRequest;
import com.taskmanager.request.UserUpdateRequest;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.respository.UserRepository;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private EmailService emailService;

	public User registerNewUser(User newUser) {
		// Realiza las validaciones necesarias antes de registrar al nuevo usuario
		if (isUsernameTaken(newUser.getUsername())) {
			throw new UsernameTakenException("El nombre de usuario ya está en uso.");
		}
		if (newUser.getName() == null || newUser.getName().trim().isEmpty()) {
			throw new InvalidNameException("El nombre no puede estar vacío o contener espacios");
		}
		if (newUser.getLastName() == null || newUser.getLastName().trim().isEmpty()) {
			throw new InvalidLastNameException("El Apellido no puede estar vacío o contener espacios");
		}
		newUser.setCreatedDate(new Date());
		String password = newUser.getPassword();

		// Verifica que la contraseña no sea nula o en blanco
		if (password == null || password.trim().isEmpty()) {
			throw new InvalidPasswordException("La contraseña no es válida.");
		}

		if (isEmailAlreadyInUse(newUser.getEmail())) {
			throw new EmailInUseException(
					"Hay una cuenta con esa dirección de correo electrónico: " + newUser.getEmail());
		}

		// Codifica y establece la contraseña
		String encodedPassword = AppConfig.passwordEncoder().encode(password);
		newUser.setPassword(encodedPassword);

		// Asigna el rol por defecto (ROLE_USER)
		Role userRole = roleRepository.findByName("ROLE_USER");
		newUser.setRole(userRole);

		// Guarda el nuevo usuario en el repositorio
		newUser = userRepository.save(newUser);

		// Genera un token de verificación y lo asocia con el usuario
		Token verificationToken = tokenService.createRegistrationToken(newUser);

		// Envia el correo de verificación
		String verificationLink = "http://localhost/api/token/verify?token=" + verificationToken.getToken();
		String emailBody = "Hola " + newUser.getName() + " " + newUser.getLastName()
				+ ", haz clic en el siguiente enlace para verificar tu cuenta: " + verificationLink;
		emailService.sendEmail(newUser.getEmail(), "Verificación de cuenta", emailBody);

		return newUser;
	}

	public void ChangePassword(ChangePasswordRequest request) {
		User user = getUser();
		if (!AppConfig.passwordEncoder().matches(request.getOldPassword(), user.getPassword())) {
			throw new PasswordMismatchException("La antigua contraseña no es válida");
		}
		String encodedNewPassword = AppConfig.passwordEncoder().encode(request.getNewPassword());
		user.setPassword(encodedNewPassword);
		userRepository.save(user);
		String subject = "Cambio de Contraseña Exitoso";
		String body = "Tu contraseña ha sido cambiada exitosamente.";

		emailService.sendEmail(user.getEmail(), subject, body);

	}

	public User updateCurrentUser(UserUpdateRequest userUpdateRequest) {
		User currentUser = getUser();
		currentUser.setName(userUpdateRequest.getName());
		currentUser.setLastName(userUpdateRequest.getLastName());
		return userRepository.save(currentUser);
	}

	public void deleteUser(String token) {
		if (tokenService.isValidToken(token)) {
			Token deleteToken = tokenService.findByToken(token);
			if (deleteToken.getUser().getUsername() == getUser().getUsername()) {
				User user = getUser();
				userRepository.delete(user);
				SecurityContextHolder.clearContext();
				tokenService.markTokenAsUsed(deleteToken);
				;

			}
		}
	}

	public void initiateUserDeletion() {

		User userToDelete = getUser();

		// Realiza cualquier validación adicional, si es necesario

		// Genera un token de confirmación para la eliminación
		Token deletionToken = tokenService.createDeletionToken(userToDelete);
		String emailBody = "Hola " + userToDelete.getName() + ",\n\n"
				+ "Hemos recibido una solicitud para eliminar tu cuenta en nuestra aplicación.\n\n"
				+ "Si deseas continuar con la eliminación de tu cuenta, por favor use el siguiente código:\n"
				+ deletionToken.getToken() + "\n\n"
				+ "Si no solicitaste la eliminación de tu cuenta, por favor ignora este correo.\n\n" + "Gracias,\n"
				+ "El equipo de [Task Manager]";
		;
		emailService.sendEmail(userToDelete.getEmail(), "Eliminación de cuenta", emailBody);
	}

	public void generatePasswordResetTokenAndSendEmail(String email) {
		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new UserNotFoundException("No se encontró un usuario con el correo electrónico: " + email);
		}

		// Crea el token de restablecimiento de contraseña
		Token passwordResetToken = tokenService.createPasswordResetToken(user);

		// Construye el enlace para restablecer la contraseña
		String resetLink = "http://localhost/reset-password?token=" + passwordResetToken.getToken();

		// Construye el cuerpo del correo electrónico
		String emailBody = "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink;

		// Envía el correo electrónico con el enlace de restablecimiento
		// Aquí deberías llamar a tu servicio de envío de correo electrónico
		// Puedes utilizar el mismo servicio de envío de correo electrónico que usas
		// para la verificación de cuenta
		emailService.sendEmail(user.getEmail(), "Verificación de cuenta", emailBody);
	}

	public void resetPassword(String token, String newPassword) {
		Token resetToken = tokenService.findByToken(token);
		if (resetToken == null) {
			throw new TokenNotFoundException("Verificación fallida: Token no válido");
		}

		if (resetToken.isExpired()) {
			throw new TokenExpiredException("El token ha expirado");
		}
		User user = resetToken.getUser();
		if (AppConfig.passwordEncoder().matches(newPassword, user.getPassword())) {
			throw new PasswordNotChangedException("La nueva contraseña debe ser diferente de la contraseña actual");
		}
		String encodedPassword = AppConfig.passwordEncoder().encode(newPassword);
		user.setPassword(encodedPassword);
		userRepository.save(user);
		tokenService.markTokenAsUsed(resetToken);
	}

	public User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByUsername(authentication.getName());
		return user;
	}

	public boolean isUsernameTaken(String username) {
		User existingUser = userRepository.findByUsername(username);
		return existingUser != null;
	}

	public boolean isEmailAlreadyInUse(String email) {
		User existingUser = userRepository.findByEmail(email);
		return existingUser != null;
	}

	public boolean isPasswordValid(User user, String password) {
		return AppConfig.passwordEncoder().matches(password, user.getPassword());
	}
}
