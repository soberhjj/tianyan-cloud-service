package com.newland.tianyan.face.controller;

import com.github.pagehelper.PageInfo;
import com.newland.face.message.NLBackend;
import com.newland.tianyan.face.common.utils.ProtobufConvertUtils;
import com.newland.tianyan.face.domain.UserInfo;
import com.newland.tianyan.face.dto.user.BackendFacesetUserCopyRequest;
import com.newland.tianyan.face.dto.user.BackendFacesetUserDeleteRequest;
import com.newland.tianyan.face.dto.user.BackendFacesetUserGetListRequest;
import com.newland.tianyan.face.dto.user.BackendFacesetUserMessageRequest;
import com.newland.tianyan.face.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage getList(@RequestBody @Validated BackendFacesetUserGetListRequest receive) {
        PageInfo<UserInfo> list = userInfoService.getList(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage(list.getList(), list.getSize());
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage copy(@RequestBody @Validated BackendFacesetUserCopyRequest receive) {
        userInfoService.copy(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Validated BackendFacesetUserDeleteRequest receive) {
        userInfoService.delete(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage();
    }

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage getInfo(@RequestBody @Validated BackendFacesetUserMessageRequest receive) {
        List<UserInfo> info = userInfoService.getInfo(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage(info, info.size());
    }
}
