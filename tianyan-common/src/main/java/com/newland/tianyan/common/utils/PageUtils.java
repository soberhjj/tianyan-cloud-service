package com.newland.tianyan.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtils {

    public static Pageable getPageable(int start, int length) {

        return start == 0 ? PageRequest.of(0, length) : PageRequest.of(start / length, length);
    }
}
