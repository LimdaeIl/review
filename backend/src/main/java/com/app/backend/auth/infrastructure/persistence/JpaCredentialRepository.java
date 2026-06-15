package com.app.backend.auth.infrastructure.persistence;

import com.app.backend.auth.domain.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCredentialRepository extends JpaRepository<Credential, Long> {

}
