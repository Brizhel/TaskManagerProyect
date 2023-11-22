package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.entity.User;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.InvalidLastNameException;
import com.taskmanager.exception.InvalidNameException;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.PasswordNotChangedException;
import com.taskmanager.exception.TokenExpiredException;
import com.taskmanager.exception.TokenNotFoundException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.request.CreateUserRequest;
import com.taskmanager.request.LoginRequest;
import com.taskmanager.response.AuthResponse;
import com.taskmanager.service.CustomUserDetailService;
import com.taskmanager.service.TokenService;
import com.taskmanager.service.UserService;
import com.taskmanager.util.JwtTokenUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private CustomUserDetailService userDetailsService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;

	@PostMapping("/signup")
	public ResponseEntity<String> registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
		try {
			@SuppressWarnings("unused")
			User registeredUser = userService.registerNewUser(createUserRequest);
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
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno del servidor" + e.getMessage());
		}
	}

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
			return ResponseEntity
					.ok("Se ha enviado un enlace para restablecer la contraseña a tu dirección de correo electrónico.");
		} catch (UserNotFoundException e) {
			return ResponseEntity.badRequest()
					.body("No se encontró ningún usuario con la dirección de correo electrónico proporcionada.");
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
			@RequestParam("password") String password) {
		try {
			userService.resetPassword(token, password);
			return ResponseEntity.ok("Cambio de contraseña realizado correctamente.");
		} catch (TokenNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verificación fallida: Token no válido");
		} catch (TokenExpiredException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El token ha expirado");
		} catch (PasswordNotChangedException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("La nueva contraseña debe ser diferente de la contraseña actual");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		String token = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
		// Extraer el token del encabezado
		String jwtToken = extractJwtFromHeader(token);
		jwtTokenUtil.invalidateToken(jwtToken);
		return ResponseEntity.ok("Logout successful!");
	}

	private String extractJwtFromHeader(String header) {
		// Verificar si el encabezado no es nulo y comienza con "Bearer "
		if (header != null && header.startsWith("Bearer ")) {
			// Devolver el token sin el prefijo "Bearer "
			return header.substring(7);
		}

		// En caso de que el formato del encabezado no sea el esperado
		return null;
	}
}
