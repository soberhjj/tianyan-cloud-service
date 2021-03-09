package com.newland.tianyan.face.event.face;


import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
//@Async
public class FaceDeleteEventListener {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @EventListener
    public void decreaseNumber(FaceDeleteEvent event) {

        // 组和用户人脸数量减少
        groupInfoMapper.faceNumberDecrease(event.getAppId(), event.getGroupId(), 1);
        userInfoMapper.faceNumberDecrease(event.getAppId(), event.getGroupId(),event.getUserId(), 1);
    }

}
