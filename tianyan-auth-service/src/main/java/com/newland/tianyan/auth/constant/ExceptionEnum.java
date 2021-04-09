package com.newland.tianyan.auth.constant;

import com.newland.tianya.commons.base.constants.ExceptionTypeEnums;
import com.newland.tianya.commons.base.support.IExceptionEnums;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/20
 */
public enum ExceptionEnum implements IExceptionEnums {
    /**
     * 存在性验证
     */
    ACCOUNT_NOT_FOUND(7400, "account[{0}] not found", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    MAIL_BOX_NOT_FOUND(7401, "mailbox[{0}] not found", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ACCOUNT_HAS_EXISTED(7402, "account[{0}] already existed", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    MAIL_BOX_HAS_EXISTED(7403, "mailbox[{0}] already existed", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ;
    private final int errorCode;

    private final String errorMsg;

    private ExceptionTypeEnums typeEnums;

    ExceptionEnum(int errorCode, String errorMsg, ExceptionTypeEnums typeEnums) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.typeEnums = typeEnums;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public ExceptionTypeEnums getTypeEnums() {
        return this.typeEnums;
    }
}
