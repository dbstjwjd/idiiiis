package com.iris.iris.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CakeType {
    CAKE("별이 빛나는 밤에"),
    POUND("만월빵 + 정통파운드"),
    CASTELLA("벌꿀카스테라 + 화과자오감(소)");

    private final String value;
}
