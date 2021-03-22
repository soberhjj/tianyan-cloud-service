package com.newland.tianyan.face.event.face;



import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.event.face.FaceCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class FaceCreateEventListener {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @EventListener
    public void increaseNumber(FaceCreateEvent event) {

        // 组和用户人脸数量增加
        groupInfoMapper.faceNumberIncrease(event.getAppId(), event.getGroupId(), 1);
        userInfoMapper.faceNumberIncrease(event.getAppId(),event.getGroupId(), event.getUserId(), 1);
    }
}
