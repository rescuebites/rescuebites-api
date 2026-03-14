package com.rescuebites.api.shared.utils;

import org.springframework.data.domain.PageRequest;

public class Constants {

    private Constants() {
    }

    public static final PageRequest PRE_FILTER_PAGE_FOR_SUGGESTIONS = PageRequest.of(0, 5);

    public static final int PRE_FETCH_SIZE = 100;

    public static final int DEFAULT_EXTRA_BY_TYPE_LIMIT = 10;
}
