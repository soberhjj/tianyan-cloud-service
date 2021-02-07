package com.newland.tianyan.face.event.face;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public class FaceCreateEvent extends FaceEvent {
    public FaceCreateEvent(Long appId, String groupId, String userId) {
        super(appId, groupId, userId);
    }
}
