package com.newland.tianyan.face.event.group;

public class GroupCreateEvent extends GroupEvent {

    public GroupCreateEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
