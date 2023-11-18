package com.taskmanager.response;

import java.util.Date;

public class UserProfileResponse {

    private String username;
    private String name;
    private String lastName;
    private String email;
    private Date createdDate;
    public UserProfileResponse(String username, String name, String lastName, String email, Date createdDate) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.createdDate = createdDate;
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
    
}
