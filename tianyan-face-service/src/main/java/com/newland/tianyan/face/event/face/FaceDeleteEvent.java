package com.newland.tianyan.face.event.face;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceDeleteEvent extends FaceEvent {

    private int deleteCount;

    public FaceDeleteEvent(Long appId, String groupId, String userId) {
        super(appId, groupId, userId);
    }

    public FaceDeleteEvent(Long appId, String groupId, String userId, int deleteCount) {
        super(appId, groupId, userId);
        this.deleteCount = deleteCount;
    }
}
