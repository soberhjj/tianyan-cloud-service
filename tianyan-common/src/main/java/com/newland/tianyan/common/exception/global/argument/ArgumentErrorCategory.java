package com.newland.tianyan.common.exception.global.argument;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public class ArgumentErrorCategory {

    public static ArgumentException getError(String code, String field) {
        switch (code) {
            case "NotBlank":
                return ArgumentErrorEnums.ARGUMENT_NOT_BLANK.toException(field);
            case "NotNull":
                return ArgumentErrorEnums.ARGUMENT_NOT_NULL.toException(field);
            case "NotEmpty":
                return ArgumentErrorEnums.ARGUMENT_NOT_EMPTY.toException(field);
            case "Max":
                return ArgumentErrorEnums.ARGUMENT_SIZE_MAN.toException(field);
            case "Min":
                return ArgumentErrorEnums.ARGUMENT_SIZE_MIN.toException(field);
            case "Pattern":
                return ArgumentErrorEnums.ARGUMENT_PATTERN.toException(field);
            default:
                return ArgumentErrorEnums.ARGUMENT_NOT_VALID.toException(field);
        }
    }
}
