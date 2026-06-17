package com.app.backend.auth.domain;

import com.app.backend.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "v1_social_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_social_provider_user",
                        columnNames = {"provider", "provider_user_id"}
                ),
                @UniqueConstraint(
                        name = "uk_social_member_provider",
                        columnNames = {"member_id", "provider"}
                )
        }
)
@Entity
public class SocialAccount extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(name = "provider_email", length = 100)
    private String providerEmail;

    private SocialAccount(
            Long memberId,
            OAuthProvider provider,
            String providerUserId,
            String providerEmail
    ) {
        this.memberId = memberId;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.providerEmail = providerEmail;
    }

    public static SocialAccount create(
            Long memberId,
            OAuthProvider provider,
            String providerUserId,
            String providerEmail
    ) {
        if (memberId == null) {
            throw new IllegalArgumentException("소셜 계정: 회원 ID는 필수입니다.");
        }

        if (provider == null) {
            throw new IllegalArgumentException("소셜 계정: provider는 필수입니다.");
        }

        if (providerUserId == null || providerUserId.isBlank()) {
            throw new IllegalArgumentException("소셜 계정: provider user id는 필수입니다.");
        }

        return new SocialAccount(
                memberId,
                provider,
                providerUserId,
                providerEmail
        );
    }
}
