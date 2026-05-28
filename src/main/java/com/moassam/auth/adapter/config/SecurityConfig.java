package com.moassam.auth.adapter.config;

import com.moassam.auth.adapter.security.jwt.JwtAuthenticationFilter;
import com.moassam.auth.adapter.security.oauth.OAuth2FailureHandler;
import com.moassam.auth.adapter.security.oauth.OAuth2SuccessHandler;
import com.moassam.auth.adapter.security.oauth.OAuthSuccessRedirectUriFilter;
import com.moassam.auth.adapter.security.oauth.SocialUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final SocialUserService socialUserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final OAuthSuccessRedirectUriFilter oauthSuccessRedirectUriFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/docs/app/**",
                                "/actuator/health",
                                "/oauth2/**",
                                "/login/**",
                                "/api/v1/auth/**",
                                "/api/v1/posts/moabang",
                                "/api/v1/posts/free",
                                "/api/v1/posts/moabang/search",
                                "/api/v1/posts/free/search"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(socialUserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                )
                .addFilterBefore(
                        oauthSuccessRedirectUriFilter,
                        OAuth2AuthorizationRequestRedirectFilter.class
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}