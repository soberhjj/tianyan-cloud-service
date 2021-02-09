package com.newland.tianyan.face.event.user;

public class UserEvent {

//    private String account;
    private Long appId;
    private String groupId;
    private String userId;

    public UserEvent(Long appId, String groupId, String userId) {
//        this.account = account;
        this.appId = appId;
        this.groupId = groupId;
        this.userId = userId;
    }

//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
