package com.newland.tianyan.auth.constant;

import lombok.Getter;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/20
 */
@Getter
public enum AuthServiceErrorEnums {
    /**
     * 存在性验证
     * */
    ACCOUNT_NOT_FOUND(5150,"account[{0}] not found"),
    MAIL_BOX_NOT_FOUND(5151,"mailbox[{0}] not found"),
    /**
     * db验证
     */
    DB_INSERT_ERROR(5100, "DBMS新增失败[{0}]"),
    DB_UPDATE_ERROR(5101, "DBMS更新失败[{0}]"),
    DB_DELETE_ERROR(5102, "DBMS删除失败[{0}]"),
    SQL_NOT_VALID(5103, "SQL语句错误[{0}:{1}]"),
    ;
    private final int errorCode;

    private final String errorMsg;

    AuthServiceErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
