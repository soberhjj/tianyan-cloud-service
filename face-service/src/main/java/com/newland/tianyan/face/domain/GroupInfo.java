package com.newland.tianyan.face.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupInfo extends BaseEntity{

    private Long id;
    private Long appId;
    private String groupId;
    private Integer userNumber;
    private Integer faceNumber;
    private String createTime;
    private String modifyTime;
    private Byte isDelete;

    public static final String TABLE_NAME = "group_info";
    public static final String ID = "id";
    public static final String APP_ID = "app_id";
    public static final String GROUP_ID = "group_id";
    public static final String USER_NUMBER = "user_number";
    public static final String FACE_NUMBER = "face_number";
    public static final String IS_DELETE = "is_delete";
    public static final String CREATE_TIME = "create_time";
    public static final String MODIFY_TIME = "modify_time";
}
