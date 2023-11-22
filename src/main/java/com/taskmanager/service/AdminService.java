package com.taskmanager.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.controller.UnauthorizedOperationException;
import com.taskmanager.entity.Role;
import com.taskmanager.entity.Token;
import com.taskmanager.entity.User;
import com.taskmanager.entity.User.UserType;
import com.taskmanager.exception.UserManagementException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.request.CreateUserRequest;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.respository.UserRepository;
import com.taskmanager.util.PasswordUtil;
import com.taskmanager.util.UserUtil;

@Service
public class AdminService {
	@Autowired
	private UserUtil userUtil;
	@Autowired
	private PasswordUtil passwordUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private EmailService emailService;
	@Autowired
	private TokenService tokenService;
	public User createAdmin(CreateUserRequest createUserRequest) {
		User newUser = modelMapper.map(createUserRequest, User.class);
		// Realiza las validaciones necesarias antes de registrar al nuevo usuario
	    userUtil.validateUserRegistration(newUser.getUsername(), newUser.getEmail(), newUser.getPassword());

		newUser.setCreatedDate(new Date());
        newUser.setUserType(UserType.ADMIN);
        String encodedPassword = passwordUtil.encryptPassword(newUser.getPassword());
        newUser.setPassword(encodedPassword);
		// Asigna el rol por defecto (ROLE_USER)
		if (newUser.getRoles() == null) {
		    newUser.setRoles(new HashSet<>());
		}
		Role userRole = roleRepository.findByName("ROLE_ADMIN");
	    newUser.getRoles().add(userRole);

		// Guarda el nuevo usuario en el repositorio
		newUser = userRepository.save(newUser);
		// Genera un token de verificación y lo asocia con el usuario
				Token verificationToken = tokenService.createRegistrationToken(newUser);

				// Envia el correo de verificación
				String verificationLink = "http://localhost/api/token/verify?token=" + verificationToken.getToken();
				String emailBody = "Hola " + userUtil.getUser().getName() + " " + userUtil.getUser().getLastName()
						+ ", hemos recibido la solicitud para crear una cuenta Administrador, por"
						+ "favor haga click en el siguiente link para verificar la cuenta: " + verificationLink;
				emailService.sendEmail(userUtil.getUser().getEmail(), "Verificación de cuenta", emailBody);
		return newUser;
	}
	public List<User> getUsersByRole(Role role) {
	        try {
	            // Verifica si se proporcionó un rol
	            if (role == null) {
	                throw new IllegalArgumentException("El rol no puede ser nulo");
	            }

	            // Retorna los usuarios que tienen el rol especificado
	            return userRepository.findByRolesContaining(role);
	        } catch (Exception e) {
	            // Captura cualquier excepción y lanza una excepción personalizada
	            throw new UserManagementException("Error al obtener la lista de usuarios por rol", e);
	        }
	    }
    public void disableUser(String username) {
        // Obtén el usuario por nombre de usuario
        User user = userRepository.findByUsername(username);
        if (user != null) {
            // Verifica si el usuario tiene el rol permitido para ser deshabilitado
            if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER"))) {
                // Realiza acciones para deshabilitar el usuario
                user.setEnabled(false);

                // Guarda el usuario actualizado en la base de datos
                userRepository.save(user);

                // Envia un correo electrónico al usuario
                emailService.sendEmail(user.getEmail(), "Tu cuenta ha sido deshabilitada", "Lamentamos informarte que tu cuenta ha sido deshabilitada. "
                        + "Si tienes preguntas, por favor contáctanos.");
            } else {
                // Manejar el caso en el que el usuario tiene un rol no permitido
                throw new UnauthorizedOperationException("No tienes permisos para deshabilitar este usuario");
            }
        } else {
            // Manejar el caso en el que el usuario no existe
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }
    }
    public void enableUser(String username) {
        // Obtén el usuario por nombre de usuario
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Verifica si el usuario tiene el rol permitido para ser habilitado
            if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER"))) {
                // Realiza acciones para habilitar el usuario
                user.setEnabled(true);

                // Guarda el usuario actualizado en la base de datos
                userRepository.save(user);

                // Envia un correo electrónico al usuario
                emailService.sendEmail(user.getEmail(), "Tu cuenta ha sido habilitada", "Te informamos que tu cuenta ha sido habilitada nuevamente. "
                        + "Si tienes preguntas, por favor contáctanos.");
            } else {
                // Manejar el caso en el que el usuario tiene un rol no permitido
                throw new UnauthorizedOperationException("No tienes permisos para habilitar este usuario");
            }
        } else {
            // Manejar el caso en el que el usuario no existe
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }
    }
	public User findUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UserNotFoundException("Usuario no encontrado");
		}
		return user;
	}
}
