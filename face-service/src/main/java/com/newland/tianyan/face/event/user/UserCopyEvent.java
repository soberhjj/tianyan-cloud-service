package com.newland.tianyan.face.event.user;

import lombok.Data;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
public class UserCopyEvent extends UserEvent{

    private int faceNumber;
    private int userNumber;

    public UserCopyEvent(Long appId, String groupId, String user_id, int faceNumber, int usernumber) {
        super(appId, groupId, user_id);
        this.faceNumber = faceNumber;
        this.userNumber = usernumber;
    }
}
