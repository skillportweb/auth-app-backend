package com.substring.auth.auth_app_backend.auth.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "refresh_token",
    indexes = {
        @Index(
            name = "refresh_tokens_jti_idx",
            columnList = "jti",
            unique = true
        ),
        @Index(
            name = "refresh_tokens_user_id_idx",
            columnList = "user_id"
        )
    }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
        name = "jti",
        unique = true,
        nullable = false,
        updatable = false
    )
    private String jti;

    @ManyToOne(
        optional = false,
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "user_id",
        nullable = false,
        updatable = false
    )
    private User user;

    @Column(
        updatable = false,
        nullable = false
    )
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    private String replacedByToken;


    // Required by JPA
    public RefreshToken() {
    }


    // =========================
    // Getters & Setters
    // =========================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public String getReplacedByToken() {
        return replacedByToken;
    }

    public void setReplacedByToken(String replacedByToken) {
        this.replacedByToken = replacedByToken;
    }


    // =========================
    // Builder
    // =========================

    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }


    public static class RefreshTokenBuilder {

        private String jti;
        private User user;
        private Instant createdAt;
        private Instant expiresAt;
        private boolean revoked;
        private String replacedByToken;


        public RefreshTokenBuilder jti(String jti) {
            this.jti = jti;
            return this;
        }


        public RefreshTokenBuilder user(User user) {
            this.user = user;
            return this;
        }


        public RefreshTokenBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }


        public RefreshTokenBuilder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }


        public RefreshTokenBuilder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }


        public RefreshTokenBuilder replacedByToken(String replacedByToken) {
            this.replacedByToken = replacedByToken;
            return this;
        }


        public RefreshToken build() {

            RefreshToken refreshToken = new RefreshToken();

            refreshToken.setJti(jti);
            refreshToken.setUser(user);
            refreshToken.setCreatedAt(createdAt);
            refreshToken.setExpiresAt(expiresAt);
            refreshToken.setRevoked(revoked);
            refreshToken.setReplacedByToken(replacedByToken);

            return refreshToken;
        }
    }
}