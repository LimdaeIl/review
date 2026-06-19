package com.app.backend.common.config;

import com.app.backend.auth.infrastructure.oauth.OAuthProviderProperties;
import com.app.backend.auth.infrastructure.oauth.OAuthRedirectProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        OAuthProviderProperties.class,
        OAuthRedirectProperties.class
})
@Configuration
public class OAuthConfig {
}
