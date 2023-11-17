package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.taskmanager.entity.User;
import com.taskmanager.entity.VerificationToken;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.respository.VerificationTokenRepository;
import com.taskmanager.service.UserService;

@RestController
@RequestMapping("/signup")
public class RegistrationController {
	
	@Autowired
	private UserService userService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
	
    @GetMapping
    public String obtenerSaludo() {
        return "¡Hola, bienvenido al API de saludos!";
    }
    
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        try {
            @SuppressWarnings("unused")
			User registeredUser = userService.registerNewUser(newUser);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (UsernameTakenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (EmailInUseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");        }
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

            if (verificationToken != null) {
                // Marcar la cuenta como verificada
                verificationToken.getUser().setVerified(true);

                // Eliminar el token o marcarlo como utilizado, según tu lógica
                verificationTokenRepository.delete(verificationToken);

                return ResponseEntity.ok("Verificación exitosa");
            } else {
                return ResponseEntity.badRequest().body("Verificación fallida: Token no válido");
            }
        } catch (Exception e) {
            // Loguear la excepción para fines de depuración
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error durante la verificación");
        }
    }
}
