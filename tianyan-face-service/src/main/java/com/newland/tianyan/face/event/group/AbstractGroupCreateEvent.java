package com.newland.tianyan.face.event.group;

public class AbstractGroupCreateEvent extends AbstractGroupEvent {

//    public GroupCreateEvent(String account, Long appId, String groupId) {
//        super(account, appId, groupId);
//    }

    public AbstractGroupCreateEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
