package com.newland.tianyan.face.event.face;

public class FaceCreateEvent extends FaceEvent {

    public FaceCreateEvent(Long appId, String groupId, String userId) {
        super(appId, groupId, userId);
    }
}
