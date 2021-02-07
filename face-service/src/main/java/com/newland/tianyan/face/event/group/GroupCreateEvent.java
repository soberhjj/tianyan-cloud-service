package com.newland.tianyan.face.event.group;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public class GroupCreateEvent extends GroupEvent{

    public GroupCreateEvent(Long appId, String groupId) {
        super(appId, groupId);
    }

}
