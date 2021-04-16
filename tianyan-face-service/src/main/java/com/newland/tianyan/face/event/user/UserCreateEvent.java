package com.newland.tianyan.face.event.user;


public class UserCreateEvent extends UserEvent {


    public UserCreateEvent(Long appId, String groupId, String userId) {
        super(appId, groupId, userId);
    }
}
