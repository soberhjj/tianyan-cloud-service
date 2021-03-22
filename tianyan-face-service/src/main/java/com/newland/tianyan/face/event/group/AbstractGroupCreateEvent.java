package com.newland.tianyan.face.event.group;

public class AbstractGroupCreateEvent extends AbstractGroupEvent {

    public AbstractGroupCreateEvent(Long appId, String groupId) {
        super(appId, groupId);
    }
}
