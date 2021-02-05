package com.newland.tianyan.face.event.group;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public class GroupDeleteEvent extends GroupEvent{

    public GroupDeleteEvent(Long appId, String groupId) {
        super(appId, groupId);
    }

}
