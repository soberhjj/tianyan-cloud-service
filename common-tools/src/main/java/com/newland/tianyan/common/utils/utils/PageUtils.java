package com.newland.tianyan.common.utils.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtils {

    public static Pageable getPageable(int start, int length) {
        if (start == 0)
            return PageRequest.of(0, length);
        else
            return PageRequest.of(start / length, length);
    }
}
