package com.moassam.observation.domain;

public enum Age {
    AGE_0, AGE_1, AGE_2, AGE_3, AGE_4, AGE_5;

    public int toYearsOld() {
        return this.ordinal();
    }
}
