package com.newland.tianyan.face.event.group;

public class GroupCreateEvent extends GroupEvent {

//    public GroupCreateEvent(String account, Long appId, String groupId) {
//        super(account, appId, groupId);
//    }

    public GroupCreateEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
