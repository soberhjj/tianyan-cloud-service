package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.constants.TaskTypeEnums;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLPage;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.dto.FaceSetGroupAddReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetGroupDeleteReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetGroupGetListReqDTO;
import com.newland.tianyan.face.service.GroupInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * 人脸库用户组信息Controller
 *
 * @Author: huangJunJie  2020-11-02 14:02
 */
@RestController
@Slf4j
@RequestMapping({"/face/group", "/backend/faceset/group", "/face/faceset/group","/backend/{version}/faceset/group", "/face/{version}/faceset/group"})
public class GroupInfoController {

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 对内&对外 创建用户组接口
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage add(@RequestBody @Valid FaceSetGroupAddReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        groupInfoService.create(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }

    /**
     * 对内&对外 获取用户组列表接口
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLPage.BackendFacesetPageMessage getList(@RequestBody @Valid FaceSetGroupGetListReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        PageInfo<GroupInfoDO> pageInfo = groupInfoService.getList(request);
        return ProtobufUtils.buildFacesetPageMessage(pageInfo.getList(), pageInfo.getSize());
    }

    /**
     * 对内&对外 删除用户组接口
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Valid FaceSetGroupDeleteReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        groupInfoService.delete(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }


}
