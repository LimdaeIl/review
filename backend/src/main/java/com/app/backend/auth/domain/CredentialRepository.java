package com.app.backend.auth.domain;

import java.util.Optional;

public interface CredentialRepository {

    void save(Credential credential);

    Optional<Credential> findByMemberId(Long id);
}
