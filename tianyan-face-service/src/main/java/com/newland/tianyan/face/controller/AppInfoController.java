package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.constants.TaskTypeEnums;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.dto.*;
import com.newland.tianyan.face.domain.entity.AppInfoDO;
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
@RequestMapping({"/app", "/backend/app", "/faceset", "/backend/faceset", "/backend/app/{version}"})
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
    public NLBackend.BackendAppSendMessage insert(@RequestBody @Validated AppCreateReqDTO receive) {
        String[] appIdList = receive.getApiList().split(",");
        for (String item : appIdList) {
            int itemInt = Integer.parseInt(item);
            if (itemInt > 5) {
                throw ExceptionSupport.toException(GlobalExceptionEnum.ARGUMENT_FORMAT_ERROR, "api_list");
            }
        }
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
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
    public NLBackend.BackendAppSendMessage delete(@RequestBody @Validated AppDeleteReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        appInfoService.delete(request);
        return ProtobufUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 更新app数据(对内接口)
     *
     * @Author Ljh
     * @Date 2020/10/21 18:19
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage update(@RequestBody @Validated AppUpdateReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        appInfoService.update(request);
        return ProtobufUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 获取一条app数据(对内接口)
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage getInfo(@RequestBody @Validated AppGetInfoReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        AppInfoDO info = appInfoService.getInfo(request);
        return ProtobufUtils.buildAppSendMessage(Collections.singletonList(info), 1);
    }

    /**
     * 获取多条app数据(对内接口)
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage findAll(@RequestBody @Validated AppGetListReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        PageInfo<AppInfoDO> pageInfo = appInfoService.getList(request);
        return ProtobufUtils.buildAppSendMessage(pageInfo.getList(), pageInfo.getSize());
    }
}
