package com.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.User;
import com.taskmanager.exception.TokenExpiredException;
import com.taskmanager.exception.TokenNotFoundException;
import com.taskmanager.entity.Token;
import com.taskmanager.respository.TokenRepository;

@Service
public class TokenService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenRepository tokenRepository;

    public Token createToken(User user, Token.TokenType tokenType) {
        Token token = new Token();
        token.setUser(user);
        token.setTokenType(tokenType);
        // Guardar el token en tu repositorio JPA
        tokenRepository.save(token);
        return token;
    }

    public Token findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void markTokenAsUsed(Token token) {
        token.setUsed(true);
        // Actualizar el token en el repositorio JPA
        tokenRepository.save(token);
    }

    public Token createRegistrationToken(User user) {
        return createToken(user, Token.TokenType.REGISTRATION);
    }

    public Token createPasswordResetToken(User user) {
        return createToken(user, Token.TokenType.PASSWORD_RESET);
    }
    public Token createDeletionToken(User user) {
    	return createToken(user,Token.TokenType.DELETION);
    }
    public void verifyToken(String token) {
        Token verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            throw new TokenNotFoundException("Verificación fallida: Token no válido");
        }

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException("El token ha expirado");
        }

        verificationToken.getUser().setVerified(true);
        User user = verificationToken.getUser();
        // Eliminar el token o marcarlo como utilizado, según tu lógica
        markTokenAsUsed(verificationToken);
        String confirmationMessage = "¡Felicidades! Tu cuenta ha sido verificada exitosamente. Gracias por registrarte.";
        emailService.sendEmail(user.getEmail(), "Confirmación de Registro", confirmationMessage);
    }
    public boolean isValidToken(String token) {
    	Token thisToken = findByToken(token);
      	if (thisToken == null) {
    		throw new TokenNotFoundException("Token inválido");
    	}
  	
    	if (thisToken.isExpired()) {
    		throw new TokenExpiredException("El token ha expirado");
    	}    	
    	return true;
    }
}

