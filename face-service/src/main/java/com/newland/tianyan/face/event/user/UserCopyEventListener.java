package com.newland.tianyan.face.event.user;

import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
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
public class UserCopyEventListener {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @EventListener
    public void increaseNumber(UserCopyEvent event) {
        groupInfoMapper.userNumberIncrease(event.getAppId(), event.getGroupId(), event.getUserNumber());
        // 人脸数添加数量为该用户人脸数
        groupInfoMapper.faceNumberIncrease(event.getAppId(), event.getGroupId(), event.getFaceNumber());
        if (event.getUserNumber() == 0){
            userInfoMapper.faceNumberIncrease(event.getAppId(), event.getGroupId(),event.getUserId(),event.getFaceNumber());
        }
    }

}
