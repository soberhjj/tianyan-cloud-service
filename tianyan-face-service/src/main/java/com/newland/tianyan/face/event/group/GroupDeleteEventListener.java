package com.newland.tianyan.face.event.group;


import com.newland.tianyan.face.dao.AppInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Author: huangJunJie  2020-11-03 14:08
 */
@Component
public class GroupDeleteEventListener {

    @Autowired
    private AppInfoMapper appInfoMapper;


    /**
     * 减少app_info表中的相关记录中的group_number的值
     *
     * @param event
     */
    @EventListener
    public void decreaseGroupNumber(GroupDeleteEvent event) {
        appInfoMapper.groupNumberIncrease(event.getAppId(), -1);
    }

}
