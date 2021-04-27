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
     * 人脸算法服务异常
     */
    RABBIT_MQ_RETURN_NONE(6001, "backend error", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    /**
     * proto异常
     */
    PROTO_PARSE_ERROR(6011, "proto转换失败", ExceptionTypeEnums.SYSTEM_EXCEPTION),
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
    EMPTY_USER_GROUP(6405, "no user in group[{0}]", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    EMPTY_FACE_GROUP(6414, "no face in group[{0}]", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    OVER_GROUP_MAX_NUMBER(6413, "group number is over limit", ExceptionTypeEnums.BUSINESS_EXCEPTION),
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
    PICTURE_HAS_NO_FACE(6409, "pic has no face", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    OVER_FACE_MAX_NUMBER(6410, "the number of user's faces is beyond the limit", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    FACE_SLOT_OVER_SIZE(6415,"faceIdSlot of userInfo is over size",ExceptionTypeEnums.SYSTEM_EXCEPTION),
    FACE_NOT_FOUND(6411, "face is not exists", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    FACE_NOT_MATCH(6412, "no matching users found", ExceptionTypeEnums.BUSINESS_EXCEPTION),
    /**
     * 质量检测
     * */
    EYE_DISTANCE_ERROR(6500,"[{0}] quality control fail! eye distance < [{1}] pixels",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ABS_PITCH_ERROR(6501,"[{0}] quality control fail! abs(pitch) >= [{1}] pixels",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ABS_YAW_ERROR(6502,"[{0}] quality control fail! abs(yaw) >= [{1}] pixels",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ABS_ROLL_ERROR(6503,"[{0}] quality control fail! abs(roll) >= [{1}] pixels",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    BLUR_ERROR(6504,"[{0}] quality control fail! blur >= [{1}] pixels",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    OCCLUSION_ERROR(6505,"[{0}] quality control fail! occlusion >=[{1}] pixels",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    BRIGHTNESS_ERROR(6506,"[{0}] quality control fail! brightness is not in [{1},{2}]",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    BRIGHTNESS_SIDE_DIFF_ERROR(6507,"[{0}] quality control fail! brightness side diff >= [{1}]",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    BRIGHTNESS_UPDOWN_ERROR(6508,"[{0}] quality control fail! brightness updown diff >= [{1}]",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    TONE_OFF_CENTER_ERROR(6509,"[{0}] quality control fail! tone off center >= [{1}]",ExceptionTypeEnums.ARGUMENT_EXCEPTION),
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
