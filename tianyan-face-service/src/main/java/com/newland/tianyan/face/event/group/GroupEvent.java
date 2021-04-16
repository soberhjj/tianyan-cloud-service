package com.newland.tianyan.face.event.group;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Administrator
 */

@Data
@AllArgsConstructor
public abstract class GroupEvent {

    private Long appId;

    private String groupId;
}
