package com.taskmanager.service;

import java.util.Date;
import java.util.HashSet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.entity.User.UserType;
import com.taskmanager.entity.Token;
import com.taskmanager.exception.TokenExpiredException;
import com.taskmanager.exception.TokenNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.request.ChangePasswordRequest;
import com.taskmanager.request.CreateUserRequest;
import com.taskmanager.request.UserUpdateRequest;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.respository.UserRepository;
import com.taskmanager.util.PasswordUtil;
import com.taskmanager.util.UserUtil;

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
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserUtil userUtil;
	@Autowired
	private PasswordUtil passwordUtil;
	public User registerNewUser(CreateUserRequest createUserRequest) {
		User newUser = modelMapper.map(createUserRequest, User.class);
	    userUtil.validateUserRegistration(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());

		newUser.setCreatedDate(new Date());
        newUser.setUserType(UserType.USER);
		String encodedPassword = passwordUtil.encryptPassword(newUser.getPassword());
		newUser.setPassword(encodedPassword);
		if (newUser.getRoles() == null) {
		    newUser.setRoles(new HashSet<>());
		}
		Role userRole = roleRepository.findByName("ROLE_USER");
	    newUser.getRoles().add(userRole);

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
		User user = userUtil.getUser();
		passwordUtil.validateOldPassword(request.getOldPassword(), user);
		String encodedNewPassword = passwordUtil.encryptPassword(request.getNewPassword());
		user.setPassword(encodedNewPassword);
		userRepository.save(user);
		String subject = "Cambio de Contraseña Exitoso";
		String body = "Tu contraseña ha sido cambiada exitosamente.";

		emailService.sendEmail(user.getEmail(), subject, body);

	}

	public User updateCurrentUser(UserUpdateRequest userUpdateRequest) {
		User currentUser = userUtil.getUser();
        modelMapper.map(userUpdateRequest, currentUser);
		return userRepository.save(currentUser);
	}

	public void deleteUser(String token) {
		if (tokenService.isValidToken(token)) {
			Token deleteToken = tokenService.findByToken(token);
			if (deleteToken.getUser().getUsername() == userUtil.getUser().getUsername()) {
				User user = userUtil.getUser();
				userRepository.delete(user);
				SecurityContextHolder.clearContext();
				tokenService.markTokenAsUsed(deleteToken);
				;

			}
		}
	}

	public void initiateUserDeletion() {

		User userToDelete = userUtil.getUser();

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
		passwordUtil.validateNewPassword(newPassword, user);
		String encodedPassword = passwordUtil.encryptPassword(newPassword);
		user.setPassword(encodedPassword);
		userRepository.save(user);
		tokenService.markTokenAsUsed(resetToken);
	}

}
