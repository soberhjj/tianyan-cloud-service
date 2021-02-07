package com.newland.tianyan.face.event.user;

import lombok.Data;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
public class UserCreateEvent extends UserEvent  {
    private int faceNumber;
    private int userNumber;

    public UserCreateEvent(Long appId, String groupId, String user_id, int faceNumber, int userNumber) {
        super(appId, groupId, user_id);
        this.faceNumber = faceNumber;
        this.userNumber = userNumber;
    }
}
