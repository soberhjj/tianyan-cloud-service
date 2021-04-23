package com.newland.tianya.commons.base.annotation;

import com.newland.tianya.commons.base.support.IntegerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IntegerValidator.class)
public @interface IntegerValid {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
