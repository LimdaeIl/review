package com.app.backend.auth.infrastructure.persistence;

import com.app.backend.auth.domain.Credential;
import com.app.backend.auth.domain.CredentialRepository;
import java.util.Optional;
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

    @Override
    public Optional<Credential> findByMemberId(Long id) {
        return repository.findByMemberId(id);
    }
}
