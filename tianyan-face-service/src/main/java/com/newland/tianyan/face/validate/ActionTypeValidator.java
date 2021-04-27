package com.newland.tianyan.face.validate;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;


/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/22
 */
public class ActionTypeValidator implements ConstraintValidator<ActionTypeValid, Object> {
    /**
     * action_type取值有两种("append"和"replace")。
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        return o == null || NULL_STRING.equals(o) || ACTION_TYPE_APPEND.equals(o) || ACTION_TYPE_REPLACE.equals(o);
    }
}
