package com.newland.tianyan.face.event.face;


import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FaceEventListener {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @EventListener
    public void increaseNumber(FaceCreateEvent event) {
        // 组和用户人脸数量增加
        groupInfoMapper.faceNumberIncrease(event.getAppId(), event.getGroupId(),
                event.getFaceNumber());
        userInfoMapper.faceNumberIncrease(event.getAppId(), event.getGroupId(),
                event.getUserId(), event.getFaceNumber(), event.getNewFaceIdSlot(false));
    }

    @EventListener
    public void decreaseNumber(FaceDeleteEvent event) {

        // 组和用户人脸数量减少
        groupInfoMapper.faceNumberDecrease(event.getAppId(), event.getGroupId(),
                event.getFaceNumber());
        userInfoMapper.faceNumberDecrease(event.getAppId(), event.getGroupId(), event.getUserId(),
                event.getFaceNumber() , event.getNewFaceIdSlot(true));
    }
}
