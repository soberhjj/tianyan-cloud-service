package com.newland.tianyan.face.event.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateEvent extends UserEvent {

    private int faceNumber;
    private int userNumber;

    public UserCreateEvent(Long appId, String groupId, String userId, int faceNumber, int userNumber) {
        super(appId, groupId, userId);
        this.faceNumber = faceNumber;
        this.userNumber = userNumber;
    }

}
