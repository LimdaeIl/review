package com.app.backend.common.config;

import com.app.backend.auth.infrastructure.oauth.OAuthProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(OAuthProviderProperties.class)
@Configuration
public class OAuthConfig {
}
