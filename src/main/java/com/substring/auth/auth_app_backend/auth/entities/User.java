package com.substring.auth.auth_app_backend.auth.entities;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;




@Entity
@Table(name = "users")
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="user_id")
   private UUID id;
	@Column(name = "user_email",unique = true,length = 300)
   private String email;
	@Column(name = "user_name",unique = true,length = 500)
   private String name;
   private String password;
   private String image;
   private boolean enable = true;
   private Instant createdAt = Instant.now();
   private Instant updateAt = Instant.now();
   
   @Enumerated(EnumType.STRING)
   private Provider provider = Provider.LOCAL;
   private String providerId;
   
   @ManyToMany(fetch = FetchType.EAGER)
     @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name="user_id"),
               inverseJoinColumns = @JoinColumn(name="role_id"))
   private Set<Role> roles= new HashSet<>();
   
   @PrePersist 
   protected void onCreate() {
	   Instant now = Instant.now();
	   if(createdAt == null) createdAt=now;
	   updateAt=now;
   }
   
   @PreUpdate
   protected void onUpdate() {
	   updateAt= Instant.now();
   }

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

   public Set<Role> getRoles() {
	return roles;
   }

   public void setRoles(Set<Role> roles) {
	this.roles = roles;
   }
   
   //Implementation class
   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
       return roles.stream()
               .map(role -> new SimpleGrantedAuthority(role.getName()))
               .toList();
   }
   
   @Override
   public String getUsername() {
       return this.email;
   }

   @Override
   public boolean isAccountNonExpired() {
       return true;
   }

   @Override
   public boolean isAccountNonLocked() {
       return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
       return true;
   }

   @Override
   public boolean isEnabled() {
       return this.enable;
   }

   public String getProviderId() {
	return providerId;
   }

   public void setProviderId(String providerId) {
	this.providerId = providerId;
   }
   
}
