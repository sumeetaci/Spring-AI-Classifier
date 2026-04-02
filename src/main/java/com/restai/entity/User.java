package com.restai.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "user_details")
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate will generate a UUID string
    @Column(name = "user_id", updatable = false, nullable = false)
    private String user_id;
    private String username;

    private String user_email;
    
    private String password;
    
    private String role;

    private String address;
    
    private String country;

    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();


	

	@Override
	public String toString() {
		return "UserDetails user_id=" + user_id + ", user_email_Id=" + user_email + ", password=****, role="
				+ role + ", address=" + address + ", country=" + country + ", username=" + username + ", created_at="
				+ created_at + "]";
	}


	


	public String getUser_email() {
		return user_email;
	}





	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}





	public String getUserEmail() {
		return user_email;
	}


	public void setUserEmail(String user_email_Id) {
		this.user_email = user_email_Id;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public LocalDateTime getCreated_at() {
		return created_at;
	}


	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

    
}
