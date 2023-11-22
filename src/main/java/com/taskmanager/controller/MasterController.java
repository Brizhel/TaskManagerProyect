package com.taskmanager.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.RoleInitializer;
import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.exception.EmailInUseException;
import com.taskmanager.exception.InvalidLastNameException;
import com.taskmanager.exception.InvalidNameException;
import com.taskmanager.exception.InvalidPasswordException;
import com.taskmanager.exception.UserManagementException;
import com.taskmanager.exception.UsernameTakenException;
import com.taskmanager.request.CreateUserRequest;
import com.taskmanager.response.UserProfileResponse;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.service.AdminService;
import com.taskmanager.service.MasterService;
import com.taskmanager.util.UserUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/master")
public class MasterController {
	private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);
    private static final Marker FILELOG = MarkerFactory.getMarker("FILELOG");
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private MasterService masterService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private AdminService adminService;
	@Autowired
	private RoleRepository roleRepository;
	  @GetMapping("/getAllUsers")
	    public List<UserProfileResponse> getAllUsers(@RequestParam String role) {
	        try {
	            Role userRole = roleRepository.findByName(role);
	            
	            if (userRole == null) {
	                throw new UserManagementException("El rol " + role + " no está configurado en la base de datos");
	            }

	            List<UserProfileResponse> userProfileResponses = adminService.getUsersByRole(userRole)
	                    .stream()
	                    .map(user -> modelMapper.map(user, UserProfileResponse.class))
	                    .collect(Collectors.toList());

	            return userProfileResponses;
	        } catch (UserManagementException e) {
	            e.printStackTrace();
	            return Collections.emptyList();
	        }
	    }
	  @RequestMapping("/register_admin")
		public ResponseEntity<String> registerAdmin(@Valid @RequestBody CreateUserRequest createUserRequest) {
			try {
				User registeredUser = adminService.createAdmin(createUserRequest);
		        logger.info(FILELOG, "Se ha creado un nuevo administrador: " + registeredUser.getUsername() );

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
		@PostMapping("/enableUser/{username}")
		public ResponseEntity<?> enableUser(@PathVariable String username) {
			try {
				masterService.enableUser(username);
		        logger.info(FILELOG, "El MASTER: " + userUtil.getUser().getUsername() + " ha habilitado al usuario " + username );

				return ResponseEntity.ok("Cuenta habilitada");
			} catch (UsernameNotFoundException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Interno.");
			}
		}
		@PostMapping("/disableUser/{username}")
		public ResponseEntity<?> disableUser(@PathVariable String username) {
			try {
				masterService.disableUser(username);
		        logger.info(FILELOG, "El MASTER: " + userUtil.getUser().getUsername() + " ha deshabilitado al usuario " + username );

				return ResponseEntity.ok("Cuenta habilitada");
			} catch (UsernameNotFoundException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Interno.");
			}
		}
}
