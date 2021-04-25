package com.newland.tianyan.face.validate;


import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.FIELD_SPLIT_REGEX;
import static com.newland.tianyan.face.constant.BusinessArgumentConstants.NULL_STRING;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/22
 */
public class FaceFieldValidator implements ConstraintValidator<FaceFieldValid, Object> {
    private Set<String> benchmarks;

    @Override
    public void initialize(FaceFieldValid constraintAnnotation) {
        benchmarks = new HashSet<>(Arrays.asList(constraintAnnotation.benchmark()));
        benchmarks.add(NULL_STRING);
    }

    /**
     * face_field取值有两种("coordinate"和"liveNess")。
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null || NULL_STRING.equals(o)) {
            return true;
        }

        String objectStr = o.toString();
        Set<String> arr = Arrays
                .stream(objectStr.split(FIELD_SPLIT_REGEX))
                .filter(item -> !StringUtils.isEmpty(item))
                .collect(Collectors.toSet());

        boolean valid = true;
        int failCount = 0;
        for (String item : arr) {
            for (String benchmark : benchmarks) {
                if (!item.equals(benchmark)) {
                    failCount++;
                }
            }
            if (failCount == benchmarks.size()) {
                valid = false;
                break;
            }
        }
        return valid;
    }
}
