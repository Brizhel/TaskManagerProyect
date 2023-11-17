package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.User;
import com.taskmanager.respository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail , usernameOrEmail);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
        }

        // Verificar el estado de verificación
        if (!user.isVerified()) {
            throw new DisabledException("La cuenta no está verificada");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("La cuenta está desactivada");
        }
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), user.getPassword(), user.getAuthorities()
        );
	}
}
	


