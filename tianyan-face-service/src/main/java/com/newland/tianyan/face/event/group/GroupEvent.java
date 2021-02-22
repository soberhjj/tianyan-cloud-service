package com.newland.tianyan.face.event.group;

public abstract class GroupEvent {

    //private String account;
    private Long appId;
    private String groupId;

//    public GroupEvent(String account, Long appId, String groupId) {
//        this.account = account;
//        this.appId = appId;
//        this.groupId = groupId;
//    }

//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }

    public GroupEvent(Long appId, String groupId){
        this.appId=appId;
        this.groupId=groupId;
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
}
