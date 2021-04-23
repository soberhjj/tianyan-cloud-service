package com.newland.tianya.commons.base.support;

import com.newland.tianya.commons.base.annotation.IntegerValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/22
 */
public class IntegerValidator implements ConstraintValidator<IntegerValid,Object> {
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("test");
        return false;
    }
}
