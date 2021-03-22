package com.newland.tianyan.auth.constant;

import com.newland.tianya.commons.base.constants.ExceptionTypeEnums;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import lombok.Getter;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/20
 */
public enum ExceptionEnum {
    /**
     * 存在性验证
     */
    ACCOUNT_NOT_FOUND(5150, "account[{0}] not found", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    MAIL_BOX_NOT_FOUND(5151, "mailbox[{0}] not found", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * db验证
     */
    DB_INSERT_ERROR(5100, "DBMS新增失败[{0}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    DB_UPDATE_ERROR(5101, "DBMS更新失败[{0}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    DB_DELETE_ERROR(5102, "DBMS删除失败[{0}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    SQL_NOT_VALID(5103, "SQL语句错误[{0}:{1}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    ;
    private final int errorCode;

    private final String errorMsg;

    private ExceptionTypeEnums typeEnums;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public ExceptionTypeEnums getTypeEnums() {
        return typeEnums;
    }

    ExceptionEnum(int errorCode, String errorMsg, ExceptionTypeEnums typeEnums) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.typeEnums = typeEnums;
    }

    public BaseException toException() {
        return ExceptionSupport.toException(getTypeEnums(), getErrorCode(), getErrorMsg());
    }

    public BaseException toException(Object... args) {
        return ExceptionSupport.toException(getTypeEnums(), getErrorCode(), getErrorMsg(), args);
    }

    public BaseException toException(Throwable throwable) {
        return ExceptionSupport.toException(getTypeEnums(), getErrorCode(), getErrorMsg(), throwable);
    }

    public BaseException toException(Throwable throwable, Object... args) {
        return ExceptionSupport.toException(getTypeEnums(), getErrorCode(), getErrorMsg(), throwable, args);
    }
}
