package com.iris.iris.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Present {
    APPLE("사과세트"),
    WINE("와인세트"),
    TUNA("참치세트"),
    REDGINSENG("정관장 에브리타임"),
    IMPACTAMIN("임팩타민");

    private final String value;
}
