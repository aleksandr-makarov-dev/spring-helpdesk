package com.aleksandrmakarovdev.helpdesk.user.entity;

import com.aleksandrmakarovdev.helpdesk.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "revoked_at")
    private Date revokedAt;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
