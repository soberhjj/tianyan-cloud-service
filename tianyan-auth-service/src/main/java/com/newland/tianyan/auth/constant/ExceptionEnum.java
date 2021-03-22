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
