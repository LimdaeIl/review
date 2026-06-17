package com.app.backend.auth.infrastructure.oauth;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientResolver {

    private final Map<OAuthProvider, OAuthClient> clients;

    public OAuthClientResolver(List<OAuthClient> oauthClients) {
        this.clients = new EnumMap<>(OAuthProvider.class);

        for (OAuthClient client : oauthClients) {
            this.clients.put(client.provider(), client);
        }
    }

    public OAuthClient resolve(OAuthProvider provider) {
        OAuthClient client = clients.get(provider);

        if (client == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }

        return client;
    }
}
