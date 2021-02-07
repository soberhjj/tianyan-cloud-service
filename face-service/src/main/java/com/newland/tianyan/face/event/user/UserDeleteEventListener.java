package com.newland.tianyan.face.event.user;

import com.newland.tianyan.face.dao.GroupInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Component
@Async
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
