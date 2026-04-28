package com.moassam.auth.adapter.integration.oauth.parser;

import com.moassam.auth.adapter.integration.oauth.UserInfo;
import com.moassam.user.domain.Provider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class KakaoParser implements UserInfoParser {

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public UserInfo parse(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = getNestedMap(attributes, "kakao_account");
        Map<String, Object> profile = getNestedMap(kakaoAccount, "profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        return new UserInfo(Provider.KAKAO, providerId, email, nickname);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> source, String key) {
        return Optional.ofNullable((Map<String, Object>) source.get(key))
                .orElse(Map.of());
    }
}