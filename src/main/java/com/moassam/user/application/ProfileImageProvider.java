package com.moassam.user.application;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Random;

@Component
public class ProfileImageProvider {

    @Value("${storage.public-base-url}")
    private String publicBaseUrl;

    private final Random random = new Random();

    public String getRandomProfileImage() {
        List<String> defaultProfileImages = List.of(
                publicUrl("profile/a.png"),
                publicUrl("profile/mo.png"),
                publicUrl("profile/ssam.png")
        );
        return defaultProfileImages.get(random.nextInt(defaultProfileImages.size()));
    }

    private String publicUrl(String key) {
        return (publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/") + key;
    }
}
