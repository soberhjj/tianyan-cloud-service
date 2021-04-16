package com.newland.tianyan.face.event.face;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/15
 */
public class FaceCreateEvent extends FaceEvent{
    public FaceCreateEvent(Long appId, String groupId, String userId, String oldFaceIdSlot, List<Long> faceIds) {
        super(appId, groupId, userId, faceIds.size(),oldFaceIdSlot, faceIds);
    }

    public FaceCreateEvent(Long appId, String groupId, String userId, String oldFaceIdSlot, Long faceId) {
        super(appId, groupId, userId,1, oldFaceIdSlot, Stream.of(faceId).collect(Collectors.toList()));
    }
}
