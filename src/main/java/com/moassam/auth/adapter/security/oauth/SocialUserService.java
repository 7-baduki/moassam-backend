package com.moassam.auth.adapter.security.oauth;

import com.moassam.auth.adapter.integration.oauth.UserInfo;
import com.moassam.auth.adapter.integration.oauth.parser.ParserFactory;
import com.moassam.auth.adapter.integration.oauth.parser.UserInfoParser;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.Provider;
import com.moassam.user.domain.User;
import com.moassam.user.domain.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SocialUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ParserFactory parserFactory;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        UserInfo userInfo = extractUserInfo(userRequest, oauth2User);

        User user = resolveUser(userInfo);

        return new SocialUser(user.getId(), user.getRole(), oauth2User.getAttributes());
    }

    private UserInfo extractUserInfo(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Provider provider = Provider.from(userRequest.getClientRegistration().getRegistrationId());
        UserInfoParser parser = parserFactory.getParser(provider);
        return parser.parse(oauth2User.getAttributes());
    }

    private User resolveUser(UserInfo userInfo) {
        return userRepository.findByProviderAndProviderId(userInfo.provider(), userInfo.providerId())
                .map(user -> reactivateIfNeeded(user, userInfo))
                .orElseGet(() -> registerNew(userInfo));
    }

    private User reactivateIfNeeded(User user, UserInfo userInfo) {
        if (user.isDeleted()) {
            user.rejoin(userInfo.email(), userInfo.nickname(), null);
            userRepository.save(user);
        }
        return user;
    }

    private User registerNew(UserInfo userInfo) {
        return userRepository.save(
                User.register(new UserRegisterRequest(
                        userInfo.provider(),
                        userInfo.providerId(),
                        userInfo.email(),
                        userInfo.nickname(),
                        null
                ))
        );
    }
}