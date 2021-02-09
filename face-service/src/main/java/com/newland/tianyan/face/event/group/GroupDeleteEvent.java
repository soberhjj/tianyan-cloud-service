package com.newland.tianyan.face.event.group;

public class GroupDeleteEvent extends GroupEvent {

//    public GroupDeleteEvent(String account, Long appId, String groupId) {
//        super(account, appId, groupId);
//    }

    public GroupDeleteEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
