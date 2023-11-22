package com.taskmanager.config;

import java.util.Collections;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.taskmanager.auth.JwtRequestFilter;
import com.taskmanager.auth.MyAuthenticationProvider;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    BouncyCastleProvider bouncyCastleProvider() {
        return new BouncyCastleProvider();
    }
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MyAuthenticationProvider myAuthenticationProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(myAuthenticationProvider);
    }
    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder.encode("adminPass"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(myAuthenticationProvider));
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http.csrf(csrf -> csrf.disable())
    	.authorizeHttpRequests(authorize -> authorize
    			.requestMatchers("/auth/logout").authenticated()
    			.requestMatchers("/auth", "/auth/**").anonymous()
    			.requestMatchers("/admin/**", "/admin").hasAnyRole("ADMIN","MASTER")
    			.requestMatchers("/user", "/user/**").authenticated()
    			.anyRequest().permitAll()
    			)
    	.httpBasic(Customizer.withDefaults())
    	.formLogin(formLogin -> formLogin
    			.loginPage("/auth/login.html"))
    	;
    	http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    	return http.build();	
    }
}

