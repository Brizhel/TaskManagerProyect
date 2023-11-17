package com.taskmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.taskmanager.service.CustomUserDetailService;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CustomUserDetailService userDetailsService;
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
    		  throws Exception {
    	        auth.userDetailsService(userDetailsService).passwordEncoder(AppConfig.passwordEncoder());

    }
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
            .password(AppConfig.passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
    @Bean
    SecurityFilterChain signupFilterChail(HttpSecurity http) throws Exception {
    	http.csrf(csrf -> csrf.disable())
    	.authorizeHttpRequests(authorize -> authorize
    			.requestMatchers("/signup").permitAll()
    			.requestMatchers("/signup/**").permitAll()
    			.anyRequest().authenticated()
    			)
    	.httpBasic(Customizer.withDefaults())
    	.formLogin(Customizer.withDefaults())
    	;
    	return http.build();
    	
    }
}

