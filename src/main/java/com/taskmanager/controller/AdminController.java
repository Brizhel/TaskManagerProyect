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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.RoleInitializer;
import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.exception.UserManagementException;
import com.taskmanager.exception.UserNotFoundException;
import com.taskmanager.response.UserProfileResponse;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.service.AdminService;
import com.taskmanager.util.UserUtil;

@RestController
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserUtil userUtils;
	@Autowired
	private AdminService adminService;
	private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);
    private static final Marker FILELOG = MarkerFactory.getMarker("FILELOG");
	  @GetMapping("/getAllUsers")
	    public List<UserProfileResponse> getAllUsers() {
	        try {
	            Role userRole = roleRepository.findByName("ROLE_USER");
	            
	            if (userRole == null) {
	                throw new UserManagementException("El rol 'ROLE_USER' no está configurado en la base de datos");
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

	@PostMapping("/disableUser/{username}")
	public ResponseEntity<?> disableUser(@PathVariable String username) {
		try {
			adminService.disableUser(username);
	        logger.info(FILELOG, "El administrador: " + userUtils.getUser().getUsername() + " ha deshabilitado al usuario " + username );
			return ResponseEntity.ok("Cuenta deshabilitada");
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		} catch(UnauthorizedOperationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Interno.");
		}
	}

	@PostMapping("/enableUser/{username}")
	public ResponseEntity<?> enableUser(@PathVariable String username) {
		try {
			adminService.enableUser(username);
	        logger.info(FILELOG, "El administrador: " + userUtils.getUser().getUsername() + " ha habilitado al usuario " + username );
			return ResponseEntity.ok("Cuenta habilitada");
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		} catch(UnauthorizedOperationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Interno.");
		}
	}

	@GetMapping("/findUser/{username}")
	public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
		try {
			User user = adminService.findUserByUsername(username);
	        UserProfileResponse userProfileResponse = modelMapper.map(user, UserProfileResponse.class);
			return ResponseEntity.ok(userProfileResponse);
		} catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	    } catch (Exception e) {
	    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Interno");
	    }
	}
}
