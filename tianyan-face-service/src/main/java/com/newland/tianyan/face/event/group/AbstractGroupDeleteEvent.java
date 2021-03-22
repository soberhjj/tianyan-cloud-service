package com.newland.tianyan.face.event.group;

public class AbstractGroupDeleteEvent extends AbstractGroupEvent {

    public AbstractGroupDeleteEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
