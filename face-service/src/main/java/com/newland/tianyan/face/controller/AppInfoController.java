package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.AppInfo;
import com.newland.tianyan.face.privateBean.*;
import com.newland.tianyan.face.service.AppInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 人脸库应用信息Controller
 **/
@RestController
@Slf4j
@RequestMapping({"/app", "/backend/app"})
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    /**
     * 新增一条app数据(对内接口)
     *
     * @Author Ljh
     * @Date 2020/10/21 16:37
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage insert(@RequestBody @Validated BackendAppCreateRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        appInfoService.insert(request);
        return ProtobufUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 删除一条app数据(对内接口)
     *
     * @Author Ljh
     * @Date 2020/10/21 18:09
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage delete(@RequestBody @Validated BackendAppDeleteRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        appInfoService.delete(request);
        return ProtobufUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 更新app数据(对内接口)
     * @Author Ljh
     * @Date 2020/10/21 18:19
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage update(@RequestBody @Validated BackendAppUpdateRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        appInfoService.update(request);
        return ProtobufUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 获取一条app数据(对内接口)
     * */
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage getInfo(@RequestBody @Validated BackendAppGetInfoRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        AppInfo info = appInfoService.getInfo(request);
        return ProtobufUtils.buildAppSendMessage(Collections.singletonList(info), 1);
    }

    /**
     * 获取多条app数据(对内接口)
     * */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage findAll(@RequestBody @Validated BackendAppGetListRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        PageInfo<AppInfo> pageInfo = appInfoService.getList(request);
        return ProtobufUtils.buildAppSendMessage(pageInfo.getList(), pageInfo.getSize());
    }
}
