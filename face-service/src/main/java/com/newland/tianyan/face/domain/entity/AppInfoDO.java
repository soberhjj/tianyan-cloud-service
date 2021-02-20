package com.newland.tianyan.face.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_info")
public class AppInfoDO extends BaseEntity {

    @Column(name = "id")
    private Long appId;
    private String account;
    private String appName;
    private String apiKey;
    private String secretKey;
    private String appInfo;
    private String apiList;
    private Integer type;
    private Integer groupNumber;
    private String createTime;
    private String modifyTime;
    private Byte isDelete;

    public static final String TABLE_NAME = "app_info";
    public static final String APP_ID = "id";
    public static final String ACCOUNT = "account";
    public static final String APP_NAME = "app_name";
    public static final String API_KEY = "api_key";
    public static final String SECRET_KEY = "secret_key";
    public static final String APP_INFO = "app_info";
    public static final String API_LIST = "api_list";
    public static final String TYPE = "type";
    public static final String GROUP_NUMBER = "group_number";
    public static final String IS_DELETE = "is_delete";
    public static final String CREATE_TIME = "create_time";
    public static final String MODIFY_TIME = "modify_time";

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }


    public String getApiList() {
        return apiList;
    }

    public void setApiList(String apiList) {
        this.apiList = apiList;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
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
