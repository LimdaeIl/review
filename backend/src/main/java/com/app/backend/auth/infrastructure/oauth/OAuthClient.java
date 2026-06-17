package com.app.backend.auth.infrastructure.oauth;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.domain.OAuthUserInfo;

public interface OAuthClient {

    OAuthProvider provider();

    OAuthUserInfo getUserInfo(String code);
}
