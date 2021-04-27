package com.newland.tianyan.face.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FaceFieldValidator.class)
public @interface FaceFieldValid {

    String[] benchmark() default {FACE_FIELD_COORDINATE, FACE_FIELD_LIVENESS};

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
