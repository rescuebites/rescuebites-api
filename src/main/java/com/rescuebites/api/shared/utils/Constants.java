package com.rescuebites.api.shared.utils;

import org.springframework.data.domain.PageRequest;

public class Constants {

    public static final PageRequest PRE_FILTER_PAGE_FOR_SEARCH = PageRequest.of(0, 100);

    public static final PageRequest PRE_FILTER_PAGE_FOR_SUGGESTIONS = PageRequest.of(0, 5);
}
