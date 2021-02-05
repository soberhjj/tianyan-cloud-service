package com.newland.tianyan.face.controller;

import com.github.pagehelper.PageInfo;
import com.newland.face.message.NLBackend;
import com.newland.tianyan.face.domain.AppInfo;
import com.newland.tianyan.face.dto.app.*;
import com.newland.tianyan.face.service.AppInfoService;
import com.newland.tianyan.face.common.utils.ProtobufConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author: RojiaHuang
 * @description: AppInfo输出能力
 * @date: 2021/2/4
 */
@RestController
@RequestMapping("/backend/app")
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
        appInfoService.insert(receive);
        return ProtobufConvertUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 删除一条app数据(对内接口)
     *
     * @Author Ljh
     * @Date 2020/10/21 18:09
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage delete(@RequestBody @Validated BackendAppDeleteRequest receive) {
        appInfoService.delete(receive);
        return ProtobufConvertUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 更新app数据(对内接口)
     *
     * @Author Ljh
     * @Date 2020/10/21 18:19
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage update(@RequestBody @Validated BackendAppUpdateRequest receive) {
        appInfoService.update(receive);
        return ProtobufConvertUtils.buildMessage(NLBackend.BackendAppSendMessage.class);
    }

    /**
     * 获取一条app数据(对内接口)
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage getInfo(@RequestBody @Validated BackendAppGetInfoRequest receive) {
        AppInfo info = appInfoService.getInfo(receive);
        return ProtobufConvertUtils.buildAppSendMessage(Collections.singletonList(info), 1);
    }

    /**
     * 获取多条app数据(对内接口)
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendAppSendMessage getList(@RequestBody @Validated BackendAppGetListRequest receive) {
        PageInfo<AppInfo> pageInfo = appInfoService.getList(receive);
        return ProtobufConvertUtils.buildAppSendMessage(pageInfo.getList(), pageInfo.getSize());
    }

    @RequestMapping(value = "/faceset/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage getListFaceset(@RequestBody @Validated BackendAppGetListRequest receive) {
        PageInfo<AppInfo> pageInfo = appInfoService.getList(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage(pageInfo.getList(), pageInfo.getSize());
    }
}
