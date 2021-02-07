package com.newland.tianyan.auth.entity;

import com.newland.common.utils.TableUtils;
import tk.mybatis.mapper.entity.IDynamicTableName;

import javax.persistence.Transient;

public class AppInfo extends BaseEntity implements IDynamicTableName {

    private Long appId;
    private String appName;
    private String apiKey;
    private String secretKey;
    private String createTime;
    private String appInfo;
    private String apiList;
    private Integer type;
    private Integer groupNumber;

    // 用于动态生成表名
    @Transient
    private String account;

    @Override
    public String getDynamicTableName() {
        return TableUtils.generateAppTableName(account);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"appId\":").append(appId);
        sb.append(",\"appName\":\"").append(appName).append('\"');
        sb.append(",\"apiKey\":\"").append(apiKey).append('\"');
        sb.append(",\"secretKey\":\"").append(secretKey).append('\"');
        sb.append(",\"createTime\":\"").append(createTime).append('\"');
        sb.append(",\"appInfo\":\"").append(appInfo).append('\"');
        sb.append(",\"apiList\":\"").append(apiList).append('\"');
        sb.append(",\"type\":").append(type);
        sb.append(",\"groupNumber\":").append(groupNumber);
        sb.append('}');
        return sb.toString();
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // ~ transient methods
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
