package com.app.backend.auth.infrastructure.persistence;

import com.app.backend.auth.domain.Credential;
import com.app.backend.auth.domain.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CredentialRepositoryImpl implements CredentialRepository {

    private final JpaCredentialRepository repository;

    @Override
    public void save(Credential credential) {
        repository.save(credential);
    }
}
