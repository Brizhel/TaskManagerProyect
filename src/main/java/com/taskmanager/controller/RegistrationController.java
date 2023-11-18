package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.taskmanager.entity.User;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.InvalidLastNameException;
import com.taskmanager.exception.InvalidNameException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/signup")
public class RegistrationController {
	
	@Autowired
	private UserService userService;

	
    @GetMapping
    public String obtenerSaludo() {
        return "¡Hola, bienvenido al API de saludos!";
    }
    
    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody User newUser) {
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
        } catch (InvalidNameException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (InvalidLastNameException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");        }
    }
}
