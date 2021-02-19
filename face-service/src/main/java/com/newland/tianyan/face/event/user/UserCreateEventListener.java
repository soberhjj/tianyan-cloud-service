package com.newland.tianyan.face.event.user;



import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.event.user.UserCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class UserCreateEventListener {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @EventListener
    public void increaseNumber(UserCreateEvent event) {
        // 组中用户数,人脸数添加
        groupInfoMapper.faceNumberIncrease(event.getAppId(), event.getGroupId(), event.getFaceNumber());
        groupInfoMapper.userNumberIncrease(event.getAppId(), event.getGroupId(), event.getUserNumber());
    }

}
