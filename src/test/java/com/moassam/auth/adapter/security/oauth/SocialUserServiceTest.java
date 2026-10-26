package com.moassam.auth.adapter.security.oauth;

import com.moassam.auth.adapter.integration.oauth.UserInfo;
import com.moassam.auth.adapter.integration.oauth.parser.ParserFactory;
import com.moassam.credit.application.provided.CreditCharger;
import com.moassam.user.application.ProfileImageProvider;
import com.moassam.user.application.required.UserLifecycleEventRepository;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.Provider;
import com.moassam.user.domain.User;
import com.moassam.user.domain.UserLifecycleEventType;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SocialUserServiceTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-25T05:30:00Z"),
            ZoneId.of("Asia/Seoul")
    );

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ProfileImageProvider profileImageProvider = mock(ProfileImageProvider.class);
    private final UserLifecycleEventRepository lifecycleRepository = mock(UserLifecycleEventRepository.class);
    private final SocialUserService service = new SocialUserService(
            userRepository,
            mock(ParserFactory.class),
            profileImageProvider,
            mock(CreditCharger.class),
            lifecycleRepository,
            CLOCK
    );

    @Test
    void resolveUser_recordsSignupForNewUser() {
        UserInfo userInfo = new UserInfo(Provider.KAKAO, "provider-1", "user@example.com", "신규");
        User savedUser = mock(User.class);
        given(savedUser.getId()).willReturn(12L);
        given(userRepository.findByProviderAndProviderId(Provider.KAKAO, "provider-1"))
                .willReturn(Optional.empty());
        given(profileImageProvider.getRandomProfileImage()).willReturn("profile");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        User result = service.resolveUser(userInfo);

        assertThat(result).isSameAs(savedUser);
        verify(lifecycleRepository).append(
                12L,
                UserLifecycleEventType.SIGNUP.name(),
                null,
                LocalDateTime.of(2026, 10, 25, 14, 30)
        );
    }

    @Test
    void resolveUser_recordsReactivationOnlyForWithdrawnUser() {
        UserInfo userInfo = new UserInfo(Provider.KAKAO, "provider-2", "again@example.com", "재가입");
        User withdrawn = mock(User.class);
        given(withdrawn.getId()).willReturn(13L);
        given(withdrawn.isDeleted()).willReturn(true);
        given(userRepository.findByProviderAndProviderId(Provider.KAKAO, "provider-2"))
                .willReturn(Optional.of(withdrawn));
        given(profileImageProvider.getRandomProfileImage()).willReturn("profile-2");

        User result = service.resolveUser(userInfo);

        assertThat(result).isSameAs(withdrawn);
        verify(withdrawn).rejoin("again@example.com", "재가입", "profile-2");
        verify(lifecycleRepository).append(
                13L,
                UserLifecycleEventType.REACTIVATION.name(),
                null,
                LocalDateTime.of(2026, 10, 25, 14, 30)
        );
    }
}
