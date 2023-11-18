package com.taskmanager.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    private TokenType tokenType;

    private boolean isUsed;

    public enum TokenType {
        REGISTRATION, PASSWORD_RESET, DELETION // Puedes agregar más tipos según tus necesidades
    }

    // Constructor, getters y setters

    public Token() {
        this.token = generateToken();
        this.expiryDate = calculateExpiryDate();
        this.isUsed = false;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private LocalDateTime calculateExpiryDate() {
        // Lógica para calcular la fecha de caducidad del token
        // Puedes establecer un período de tiempo desde la creación del token
        return LocalDateTime.now().plusDays(1); // Ejemplo: expira en 1 día
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}

