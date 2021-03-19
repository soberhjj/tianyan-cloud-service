package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.constants.TaskTypeEnums;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.ProtobufUtils;
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


/**
 * 人脸库用户组信息Controller
 *
 * @Author: huangJunJie  2020-11-02 14:02
 */
@RestController
@Slf4j
@RequestMapping({"/face/group", "/backend/faceset/group", "/face/faceset/group","/backend/faceset/group/{version}", "/face/faceset/group/{version}"})
public class GroupInfoController {

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 对内&对外 创建用户组接口
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage add(@RequestBody @Validated FaceSetGroupAddReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        groupInfoService.create(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }

    /**
     * 对内&对外 获取用户组列表接口
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage checkUnique(@RequestBody @Validated FaceSetGroupGetListReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        PageInfo<GroupInfoDO> pageInfo = groupInfoService.getList(request);
        return ProtobufUtils.buildFacesetSendMessage(pageInfo.getList(), pageInfo.getSize());
    }

    /**
     * 对内&对外 删除用户组接口
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Validated FaceSetGroupDeleteReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        groupInfoService.delete(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }


}
