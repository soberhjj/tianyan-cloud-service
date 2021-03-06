package com.newland.tianyan.face.event.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEvent {

    private Long appId;

    private String groupId;

    private String userId;
}
