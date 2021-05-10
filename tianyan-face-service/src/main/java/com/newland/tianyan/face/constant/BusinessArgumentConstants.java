package com.newland.tianyan.face.constant;

/**
 * @author: RojiaHuang
 * @description: 业务限制参数
 * @date: 2021/3/9
 */
public class BusinessArgumentConstants {
    public static final String ID_SPLIT_REGEX = ",";

    public static final String FIELD_SPLIT_REGEX = ",";

    public static final Integer DEFAULT_PAGE_INDEX = 0;

    public static final Integer DEFAULT_PAGE_SIZE = 100;

    public static final Long MIN_APP_ID = 1599613749000L;
    /**
     * 单次请求用户组的数量上限
     * */
    public static final int SEARCH_MAX_GROUP_NUMBER = 10;

    public static final int MAX_GROUP_LENGTH = 32;
    /**
     * 单个用户限制添加最多人脸数目
     */
    public static final int MAX_FACE_NUMBER = 10;
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

    public static final String FACE_TASK_TYPE_COORDINATE = "coordinate";

    public static final String FACE_TASK_TYPE_FEATURE = "feature";

    public static final String FACE_TASK_TYPE_LIVENESS = "liveness";

    public static final String FACE_TASK_TYPE_MULTIATTRIBUTE = "multiAttribute";

    public static final String NULL_STRING = "null";

    public static final String FACE_TASK_TYPE_InteractLIVENESS = "interLiveness";

}
