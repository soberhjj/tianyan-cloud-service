package com.newland.tianyan.face.event.group;

public class AbstractGroupDeleteEvent extends AbstractGroupEvent {

//    public GroupDeleteEvent(String account, Long appId, String groupId) {
//        super(account, appId, groupId);
//    }

    public AbstractGroupDeleteEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
