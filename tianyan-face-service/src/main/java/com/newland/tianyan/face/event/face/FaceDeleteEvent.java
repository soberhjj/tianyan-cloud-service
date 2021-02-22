package com.newland.tianyan.face.event.face;

public class FaceDeleteEvent extends FaceEvent {

    public FaceDeleteEvent(Long appId, String groupId, String userId) {
        super(appId, groupId, userId);
    }
}
