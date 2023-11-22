package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.User;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.respository.UserRepository;
@Service
public class MasterService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmailService emailService;

    public void disableUser(String username) {
        // Obtén el usuario por nombre de usuario
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Realiza acciones para deshabilitar el usuario
            user.setEnabled(false);

            // Guarda el usuario actualizado en la base de datos
            userRepository.save(user);

            // Envia un correo electrónico al usuario
            emailService.sendEmail(user.getEmail(), "Tu cuenta ha sido deshabilitada", "Lamentamos informarte que tu cuenta ha sido deshabilitada. "
                    + "Si tienes preguntas, por favor contáctanos.");
        } else {
            // Manejar el caso en el que el usuario no existe
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }
    }
    public void enableUser(String username) {
        // Obtén el usuario por nombre de usuario
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Realiza acciones para habilitar el usuario
            user.setEnabled(true);

            // Guarda el usuario actualizado en la base de datos
            userRepository.save(user);

            // Envia un correo electrónico al usuario
            emailService.sendEmail(user.getEmail(), "Tu cuenta ha sido habilitada", "Te informamos que tu cuenta ha sido habilitada nuevamente"
                    + "Si tienes preguntas, por favor contáctanos.");
        } else {
            // Manejar el caso en el que el usuario no existe
            throw new UserNotFoundException("Usuario no encontrado: " + username);
        }
    }
}
