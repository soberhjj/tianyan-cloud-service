package com.newland.tianyan.face.event.group;

import com.newland.tianyan.face.dao.AppInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Async
@Component
public class GroupCreateEventListener {
    @Autowired
    private AppInfoMapper appInfoMapper;

    /**
     * 增加app_info表中的相关记录中的group_number的值
     *
     * @param event
     */
    @EventListener
    public void increaseGroupNumber(GroupCreateEvent event) {
        appInfoMapper.groupNumberIncrease(event.getAppId(), 1);
    }

}
