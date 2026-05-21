package com.moassam.observation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Age {
    AGE_0(0),
    AGE_1(1),
    AGE_2(2),
    AGE_3(3),
    AGE_4(4),
    AGE_5(5);

    private final int value;
}
