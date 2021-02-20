package com.newland.tianyan.face.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: huangJunJie  2020-11-02 14:15
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfo extends BaseEntity {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public Integer getFaceNumber() {
        return faceNumber;
    }

    public void setFaceNumber(Integer faceNumber) {
        this.faceNumber = faceNumber;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }
}
