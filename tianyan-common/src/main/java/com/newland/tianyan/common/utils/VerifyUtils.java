package com.newland.tianyan.common.utils;

import com.newland.tianyan.common.exception.CommonException;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * VO参数验证工具类
 *
 * @author Administrator
 */
public class VerifyUtils {
    private static Validator validator = ((HibernateValidatorConfiguration) Validation.byProvider(HibernateValidator.class).configure()).failFast(true).buildValidatorFactory().getValidator();
    public static final String REGEX_NUMBER = "^\\s{0}|\\d+$";
    public static final String REGEX_NUMBER_LENGTH = "^\\d{%d}$";
    public static final String REGEX_YEAR = "^\\d{4}$";
    public static final String REGEX_MONTH= "^\\d{4}-\\d{1,2}";
    public static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    public static final String REGEX_TIME = "\\s{0}|(([01]\\d)|(2[0-3]))[0-5]\\d([0-5]\\d)?";
    public static final String REGEX_DATETIME = "^\\s{0}|\\d{4}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3([0|1])))((0[0-9])|([1-2][0-9]))((0[0-9])|([1-5][0-9]))((0[0-9])|([1-5][0-9]))$";

    public VerifyUtils() {
    }

    public static <T> void validate(T obj) throws CommonException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj, new Class[0]);
        if (constraintViolations.size() > 0) {
            ConstraintViolation<T> validateInfo = (ConstraintViolation) constraintViolations.iterator().next();
            //throw ApiReturnErrorCode.ARGUMENT_NOT_VALID.toException(validateInfo.getInvalidValue(), validateInfo.getMessage());
        }
    }

}
