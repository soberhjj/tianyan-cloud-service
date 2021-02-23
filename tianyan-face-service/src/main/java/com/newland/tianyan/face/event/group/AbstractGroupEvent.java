package com.newland.tianyan.face.event.group;

/**
 * @author Administrator
 */
public abstract class AbstractGroupEvent {

    private Long appId;
    private String groupId;

    public AbstractGroupEvent(Long appId, String groupId) {
        this.appId = appId;
        this.groupId = groupId;
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
