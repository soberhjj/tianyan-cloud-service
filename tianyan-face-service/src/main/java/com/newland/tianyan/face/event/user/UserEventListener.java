package com.newland.tianyan.face.event.user;


import com.newland.tianyan.face.dao.GroupInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @EventListener
    public void increaseNumber(UserCreateEvent event) {
        // 组中用户数,人脸数添加
        groupInfoMapper.userNumberIncrease(event.getAppId(), event.getGroupId(), 1);
    }

    @EventListener
    public void decreaseNumber(UserDeleteEvent event) {
        // 组中用户数,人脸数减少
        groupInfoMapper.userNumberDecrease(event.getAppId(), event.getGroupId(), 1, event.getFaceNumber());
    }
}
