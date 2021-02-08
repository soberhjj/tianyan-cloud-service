package com.newland.tianyan.face.common.exception;

import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.text.MessageFormat;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/29
 */
public class ArgumentErrorConvert {

    private final static int VALID_FAIL = 6100;
    private final static int FORMAT_FAIL = 6101;

    public static FaceServiceException toException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        assert fieldError != null;
        String code = fieldError.getCode();

        String defaultMessage = fieldError.getDefaultMessage();
        if ("NotNull".equals(code)) {
            defaultMessage = formatMsg("param [{0}] is null", fieldError.getField());
            return new FaceServiceException(VALID_FAIL, defaultMessage);
        } else if ("Min".equals(code)) {
            defaultMessage = formatMsg("param [{0}] length error", fieldError.getField());
            return new FaceServiceException(VALID_FAIL, defaultMessage);
        } else if ("Range".equals(code)) {
            defaultMessage = formatMsg("param [{0}] range error", fieldError.getField());
            return new FaceServiceException(VALID_FAIL, defaultMessage);
        } else if ("Pattern".equals(code)) {
            defaultMessage = formatMsg("param [{0}] is format error", fieldError.getField());
            return new FaceServiceException(FORMAT_FAIL, defaultMessage);
        } else if ("NotBlank".equals(code)) {
            defaultMessage = formatMsg("param [{0}] is format error", fieldError.getField());
            return new FaceServiceException(VALID_FAIL, defaultMessage);
        }
        defaultMessage = StringUtils.isEmpty(defaultMessage) ? formatMsg("param [{0}] is format error", fieldError.getField()) : defaultMessage;
        return new FaceServiceException(FORMAT_FAIL, defaultMessage);
    }

    protected static String formatMsg(String str, Object... args) {
        return args != null && args.length != 0 ? MessageFormat.format(str, args) : str;
    }
}
