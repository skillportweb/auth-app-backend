package com.substring.auth.auth_app_backend.auth.payload;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.substring.auth.auth_app_backend.auth.entities.Provider;

public class UserDto {
	
   private UUID id;
   private String email;
   private String name;
   private String password;
   private String image;
   private boolean enable = true;
   private Instant createdAt = Instant.now();
   private Instant updateAt = Instant.now();
   
   private Provider provider = Provider.LOCAL;
   private Set<RoleDto> roles= new HashSet<>();
   
   public UUID getId() {
	return id;
   }
   public void setId(UUID id) {
	this.id = id;
   }
   public String getEmail() {
	return email;
   }
   public void setEmail(String email) {
	this.email = email;
   }
   public String getName() {
	return name;
   }
   public void setName(String name) {
	this.name = name;
   }
   public String getPassword() {
	return password;
   }
   public void setPassword(String password) {
	this.password = password;
   }
   public String getImage() {
	return image;
   }
   public void setImage(String image) {
	this.image = image;
   }
   public boolean isEnable() {
	return enable;
   }
   public void setEnable(boolean enable) {
	this.enable = enable;
   }
   public Instant getCreatedAt() {
	return createdAt;
   }
   public void setCreatedAt(Instant createdAt) {
	this.createdAt = createdAt;
   }
   public Instant getUpdateAt() {
	return updateAt;
   }
   public void setUpdateAt(Instant updateAt) {
	this.updateAt = updateAt;
   }
   public Provider getProvider() {
	return provider;
   }
   public void setProvider(Provider provider) {
	this.provider = provider;
   }
   public Set<RoleDto> getRoles() {
	return roles;
   }
   public void setRoles(Set<RoleDto> roles) {
	this.roles = roles;
   }
   
}
