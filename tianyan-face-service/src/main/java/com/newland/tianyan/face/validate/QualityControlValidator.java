package com.newland.tianyan.face.validate;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/22
 */
public class QualityControlValidator implements ConstraintValidator<QualityControlValid, Object> {

    /**
     * Quality_Control取值有1-3。
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Integer value = (Integer) o;
        return value == null || value == 0 || value == 1 || value == 2 || value == 3;
    }
}
