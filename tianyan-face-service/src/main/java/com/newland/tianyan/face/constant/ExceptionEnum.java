package com.newland.tianyan.face.constant;

import com.newland.tianya.commons.base.constants.ExceptionTypeEnums;
import com.newland.tianya.commons.base.support.IExceptionEnums;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/20
 */
public enum ExceptionEnum implements IExceptionEnums {
    /**
     * 存在性异常
     */
    WRONG_FACE_FIELD(6311, "param [face_field] format error", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    WRONG_ACTION_TYPE(6312, "param [action_type] format error", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    /**
     * app
     */
    APP_NOT_FOUND(6401, "app[{0}] is not found", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    APP_ALREADY_EXISTS(6402, "app[{0}] already exists", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    /**
     * group
     */
    GROUP_NOT_FOUND(6403, "group[{0}] is not found", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    GROUP_ALREADY_EXISTS(6404, "group[{0}] already exists", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    EMPTY_GROUP(6405, "no user in group[{0}]", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    /**
     * user
     */
    USER_NOT_FOUND(6406, "user[{0}] is not found", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    USER_ALREADY_EXISTS(6407, "user[{0}] already exists", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    //(单个group上线最大5万用户)
    OVER_USE_MAX_NUMBER(6408, "request add user over limit", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    /**
     * face
     */
    FACE_NOT_FOUND(6409, "face is not found", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    OVER_FACE_MAX_NUMBER(6410, "the number of user's faces is beyond the limit", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    /**
     * 人脸算法服务异常
     */
    RABBIT_MQ_RETURN_NONE(6001, "backend error", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    /**
     * db验证
     */
    DB_INSERT_ERROR(6002, "DBMS insert fail [{0}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    DB_UPDATE_ERROR(6003, "DBMS update fail [{0}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    DB_DELETE_ERROR(6004, "DBMS delete fail[{0}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    SQL_NOT_VALID(6005, "SQL fail [{0}:{1}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    /**
     * 向量集合删除失败
     **/
    VECTOR_DROP_ERROR(6006, "vector drop fail", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    VECTOR_CREATE_ERROR(6007, "vector create collection fail", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    VECTOR_INSERT_ERROR(6008, "vector insert fail", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    VECTOR_DELETE_ERROR(6009, "vector delete fail", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    VECTOR_QUERY_ERROR(6010, "vector query fail", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    /**
     * proto异常
     */
    PROTO_PARSE_ERROR(6011, "proto转换失败", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    ;
    private final int errorCode;

    private final String errorMsg;

    private final ExceptionTypeEnums typeEnums;

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public ExceptionTypeEnums getTypeEnums() {
        return typeEnums;
    }

    ExceptionEnum(int errorCode, String errorMsg, ExceptionTypeEnums typeEnums) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.typeEnums = typeEnums;
    }

}
