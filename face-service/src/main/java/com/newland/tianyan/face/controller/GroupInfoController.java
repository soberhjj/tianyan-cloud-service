package com.newland.tianyan.face.controller;

import com.github.pagehelper.PageInfo;
import com.newland.face.message.NLBackend;
import com.newland.tianyan.face.common.utils.ProtobufConvertUtils;
import com.newland.tianyan.face.domain.GroupInfo;
import com.newland.tianyan.face.dto.group.BackendFacesetGroupAddRequest;
import com.newland.tianyan.face.dto.group.BackendFacesetGroupDeleteRequest;
import com.newland.tianyan.face.dto.group.BackendFacesetGroupGetListRequest;
import com.newland.tianyan.face.service.GroupInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description: 人脸库用户组信息Controller
 * @date: 2021/2/4
 */
@RestController
@Slf4j
@RequestMapping({"/backend/faceset/group", "/face/faceset/group"})
public class GroupInfoController {
    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 对内&对外 创建用户组接口
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage add(@RequestBody @Validated BackendFacesetGroupAddRequest receive) {
        groupInfoService.create(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage();
    }

    /**
     * 对内&对外 获取用户组列表接口
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage checkUnique(@RequestBody @Validated BackendFacesetGroupGetListRequest receive) {
        PageInfo<GroupInfo> pageInfo = groupInfoService.getList(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage(pageInfo.getList(), pageInfo.getSize());
    }

    /**
     * 对内&对外 删除用户组接口
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Validated BackendFacesetGroupDeleteRequest receive) {
        groupInfoService.delete(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage();
    }

}
