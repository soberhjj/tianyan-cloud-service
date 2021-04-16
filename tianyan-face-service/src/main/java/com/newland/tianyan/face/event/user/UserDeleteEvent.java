package com.newland.tianyan.face.event.user;

import lombok.Getter;

@Getter
public class UserDeleteEvent extends UserEvent {

    private Integer faceNumber;

    public UserDeleteEvent(Long appId, String groupId,  Integer faceNumber) {
        super(appId, groupId, null);
        this.faceNumber = faceNumber;
    }
}
