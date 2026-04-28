package com.moassam.auth.adapter.integration.oauth.parser;

import com.moassam.auth.adapter.integration.oauth.UserInfo;
import com.moassam.user.domain.Provider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class NaverParser implements UserInfoParser {

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public UserInfo parse(Map<String, Object> attributes) {
        Map<String, Object> response = getNestedMap(attributes, "response");

        String providerId = (String) response.get("id");
        String email = (String) response.get("email");
        String nickname = (String) response.get("name");

        return new UserInfo(Provider.NAVER, providerId, email, nickname);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> source, String key) {
        return Optional.ofNullable((Map<String, Object>) source.get(key))
                .orElse(Map.of());
    }
}