package com.newland.tianyan.face.event.user;


import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.event.user.UserDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserDeleteEventListener {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @EventListener
    public void decreaseNumber(UserDeleteEvent event) {

        // 组中用户数,人脸数减少
        groupInfoMapper.userNumberDecrease(event.getAppId(), event.getGroupId(), event.getUserNumber());
        groupInfoMapper.faceNumberDecrease(event.getAppId(), event.getGroupId(), event.getFaceNumber());
    }
}
