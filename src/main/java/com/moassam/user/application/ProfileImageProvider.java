package com.moassam.user.application;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class ProfileImageProvider {

    private static final List<String> DEFAULT_PROFILE_IMAGES = List.of(
            "https://kr.object.ncloudstorage.com/moassam-storage/profile/a.png",
            "https://kr.object.ncloudstorage.com/moassam-storage/profile/mo.png",
            "https://kr.object.ncloudstorage.com/moassam-storage/profile/ssam.png"
    );

    private final Random random = new Random();

    public String getRandomProfileImage() {
        return DEFAULT_PROFILE_IMAGES.get(random.nextInt(DEFAULT_PROFILE_IMAGES.size()));
    }
}