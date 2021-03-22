package com.newland.tianyan.face.constant;

/**
 * @author: RojiaHuang
 * @description: 业务限制参数
 * @date: 2021/3/9
 */
public class BusinessArgumentConstants {
    /**
     * 单个用户限制添加最多人脸数目
     */
    public static final int MAX_FACE_NUMBER = 20;
    /**
     * 单个用户组限制添加最多用户数目
     */
    public static final int MAX_USER_NUMBER = 50000;
    /**
     * 人脸注册操作类型类型
     */
    public static final String ACTION_TYPE_APPEND = "append";

    public static final String ACTION_TYPE_REPLACE = "replace";
    /**
     * 人脸特征获取类型
     */
    public static final String FACE_FIELD_COORDINATE = "coordinate";

    public static final String FACE_FIELD_LIVENESS = "liveness";
}
