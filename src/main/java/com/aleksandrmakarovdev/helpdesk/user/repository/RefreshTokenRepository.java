package com.aleksandrmakarovdev.helpdesk.user.repository;

import com.aleksandrmakarovdev.helpdesk.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

}
