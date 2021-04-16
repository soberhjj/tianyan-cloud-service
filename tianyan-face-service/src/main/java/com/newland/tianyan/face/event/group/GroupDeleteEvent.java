package com.newland.tianyan.face.event.group;

public class GroupDeleteEvent extends GroupEvent {

    public GroupDeleteEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
