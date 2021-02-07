package com.newland.tianyan.face.event.face;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public class FaceDeleteEvent extends FaceEvent {

    public FaceDeleteEvent(Long appId, String groupId, String userId) {
        super(appId, groupId, userId);
    }
}
