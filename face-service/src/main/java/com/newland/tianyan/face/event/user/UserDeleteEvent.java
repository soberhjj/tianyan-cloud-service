package com.newland.tianyan.face.event.user;

import lombok.Data;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
public class UserDeleteEvent extends UserEvent {

    private int faceNumber;
    private int userNumber;

    public UserDeleteEvent(Long appId, String groupId, String userId, int faceNumber, int userNumber) {
        super(appId, groupId, userId);
        this.faceNumber = faceNumber;
        this.userNumber = userNumber;
    }
}
