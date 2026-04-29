package com.moassam.auth.adapter.integration.oauth.parser;

import com.moassam.auth.adapter.integration.oauth.UserInfo;
import com.moassam.user.domain.Provider;

import java.util.Map;

public interface UserInfoParser {

    Provider getProvider();

    UserInfo parse(Map<String, Object> attributes);
}