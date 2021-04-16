package com.newland.tianyan.face.event.face;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/15
 */
public class FaceDeleteEvent extends FaceEvent {

    public FaceDeleteEvent(Long appId, String groupId, String userId, Integer faceNumber, String oldFaceIdSlot, Long faceId) {
        super(appId, groupId, userId, faceNumber, oldFaceIdSlot, Stream.of(faceId).collect(Collectors.toList()));
    }

    public FaceDeleteEvent(Long appId, String groupId, String userId, Integer faceNumber, String oldFaceIdSlot, List<Long> faceIds) {
        super(appId, groupId, userId, faceNumber, oldFaceIdSlot, faceIds);
    }
}
