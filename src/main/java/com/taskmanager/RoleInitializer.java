package com.taskmanager;

import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.entity.User.UserType;
import com.taskmanager.exception.RoleNotFoundException;
import com.taskmanager.respository.RoleRepository;
import com.taskmanager.respository.UserRepository;
import com.taskmanager.util.PasswordUtil;
@Component
public class RoleInitializer implements CommandLineRunner{
	private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);
    private static final Marker FILELOG = MarkerFactory.getMarker("FILELOG");
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public void run(String... args) throws Exception {
        // Verifica si los roles ya existen en la base de datos
    	initializeRoles();
        initializeMasterUser();
        logger.info("Este mensaje se verá en la consola y en el archivo.");
        logger.info(FILELOG, "Este mensaje se verá solo en el archivo.");
    }
    private void initializeRoles() {
        // Crea el rol ROLE_MASTER si no existe
        Role masterRole = roleRepository.findByName("ROLE_MASTER");
        if (masterRole == null) {
            masterRole = new Role();
            masterRole.setName("ROLE_MASTER");
            roleRepository.save(masterRole);
        }
        if (roleRepository.findByName("ROLE_USER") == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        if (roleRepository.findByName("ROLE_MASTER") == null) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_MASTER");
            roleRepository.save(adminRole);
        }
    }

    private void initializeMasterUser() {
        // Crea el usuario Master si no existe
        User masterUser = userRepository.findByUsername("Brizhel");
        if (masterUser == null) {
            masterUser = new User();
            masterUser.setUsername("Brizhel");
            masterUser.setPassword(passwordUtil.encryptPassword("Reny30320104!!"));
            masterUser.setName("Reny");
            masterUser.setLastName("Mireles");
            masterUser.setEmail("renymireles@outlook.com");
            masterUser.setCreatedDate(new Date());
            masterUser.setUserType(UserType.MASTER);
            masterUser.setVerified(true);
            // Asigna el rol ROLE_MASTER
            Role masterRole = roleRepository.findByName("ROLE_MASTER");
            if (masterRole == null) {
                throw new RoleNotFoundException("El rol ROLE_MASTER no está configurado en la aplicación.");
            }
    		if (masterUser.getRoles() == null) {
    		    masterUser.setRoles(new HashSet<>());
    		}
            masterUser.getRoles().add(masterRole);

            userRepository.save(masterUser);
        }
    }

}
