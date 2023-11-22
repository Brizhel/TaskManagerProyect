package com.taskmanager.response;

import java.util.Date;

import com.taskmanager.entity.User.UserType;

public class UserProfileResponse {

    private String username;
    private String name;
    private String lastName;
    private String email;
    private Date createdDate;
    private UserType userType; // Agrega el campo UserType
    public UserProfileResponse(String username, String name, String lastName, String email, Date createdDate) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.createdDate = createdDate;
    }
    public UserProfileResponse() {
    }
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Date getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	public UserType getUserType() {
		return userType;
	}


	public void setUserType(UserType userType) {
		this.userType = userType;
	}
    
}
