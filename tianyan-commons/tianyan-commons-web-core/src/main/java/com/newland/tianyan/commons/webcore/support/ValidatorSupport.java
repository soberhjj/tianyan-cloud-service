package com.newland.tianyan.commons.webcore.support;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/21
 */
public class ValidatorSupport {
    /**
     * 使用hibernate的注解来进行验证
     */
    private static Validator validator = Validation
            .byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();

    public static <T> void validate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        // 抛出检验异常
        if (constraintViolations.size() > 0) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.ARGUMENT_FORMAT_ERROR, constraintViolations.iterator().next().getMessage());
        }
    }
}
