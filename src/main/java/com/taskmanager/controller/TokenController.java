package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.exception.PasswordNotChangedException;
import com.taskmanager.exception.TokenExpiredException;
import com.taskmanager.exception.TokenNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.service.TokenService;
import com.taskmanager.service.UserService;

@RestController
@RequestMapping("/api/token")
public class TokenController {
	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        try {
        	tokenService.verifyToken(token);
            return ResponseEntity.ok("Verificación exitosa");
        } catch (TokenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verificación fallida: Token no válido");
        } catch (TokenExpiredException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado");
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        try {
             userService.generatePasswordResetTokenAndSendEmail(email);
            return ResponseEntity.ok("Se ha enviado un enlace para restablecer la contraseña a tu dirección de correo electrónico.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body("No se encontró ningún usuario con la dirección de correo electrónico proporcionada.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
    	try {
    		userService.resetPassword(token, password); 
    		return ResponseEntity.ok("Cambio de contraseña realizado correctamente.");
        } catch (TokenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verificación fallida: Token no válido");
        } catch (TokenExpiredException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado");
        } catch (PasswordNotChangedException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La nueva contraseña debe ser diferente de la contraseña actual");
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}

