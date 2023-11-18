package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.entity.User;
import com.taskmanager.exception.PasswordMismatchException;
import com.taskmanager.exception.TokenExpiredException;
import com.taskmanager.exception.TokenNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.request.ChangePasswordRequest;
import com.taskmanager.request.UserUpdateRequest;
import com.taskmanager.response.UserProfileResponse;
import com.taskmanager.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile() {
	    try {
	        // Obtén la información del usuario actual (puede depender de la autenticación del usuario)
	        User currentUser = userService.getUser();
	        UserProfileResponse userProfileResponse = new UserProfileResponse(
	                currentUser.getUsername(),
	                currentUser.getName(),
	                currentUser.getLastName(),
	                currentUser.getEmail(),
	                currentUser.getCreatedDate()
	        );
	        return ResponseEntity.ok(userProfileResponse);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
	    }
	}
	@PatchMapping("/profile")
	public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserUpdateRequest userUpdateRequest) {
	    try {
	        // Actualiza la información del usuario actual (puede depender de la autenticación del usuario)
	        User updatedUser = userService.updateCurrentUser(userUpdateRequest);
	        return ResponseEntity.ok(updatedUser);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
	    }
	}
	@DeleteMapping("/delete")
	public ResponseEntity<String> initiateUserDeletion() {
        try {
            userService.initiateUserDeletion();
            return ResponseEntity.ok("Se ha enviado un correo de confirmación para eliminar la cuenta");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
	@PostMapping("/delete")
	public ResponseEntity<String> performDelete(@RequestParam String token){
		try {
			userService.deleteUser(token);
			return ResponseEntity.ok("Cuenta Borrada");
		} catch (TokenExpiredException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (TokenNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request){
		try {
			userService.ChangePassword(request);
			return ResponseEntity.ok("Contraseña modificada");
		} catch (PasswordMismatchException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
		}
	}
}
