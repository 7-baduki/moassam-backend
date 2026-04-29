package com.moassam.auth.adapter.integration.oauth.parser;

import com.moassam.auth.exception.AuthErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.domain.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParserFactory {

    private final Map<Provider, UserInfoParser> parsers;

    public ParserFactory(List<UserInfoParser> parsers) {
        this.parsers = parsers.stream()
                .collect(Collectors.toMap(UserInfoParser::getProvider, Function.identity()));
    }

    public UserInfoParser getParser(Provider provider) {
        return Optional.ofNullable(parsers.get(provider))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.UNSUPPORTED_PROVIDER));
    }
}