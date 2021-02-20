package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.model.proto.ProtobufUtils;
import com.newland.tianyan.face.entity.GroupInfo;
import com.newland.tianyan.face.vo.FaceSetGroupAddReq;
import com.newland.tianyan.face.vo.FaceSetGroupDeleteReq;
import com.newland.tianyan.face.vo.FaceSetGroupGetListReq;
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
@RequestMapping({"/face/group", "/backend/faceset/group", "/face/faceset/group"})
public class GroupInfoController {

    @Autowired
    private GroupInfoService groupInfoService;

    /**
     * 对内&对外 创建用户组接口
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage add(@RequestBody @Validated FaceSetGroupAddReq receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        groupInfoService.create(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }

    /**
     * 对内&对外 获取用户组列表接口
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage checkUnique(@RequestBody @Validated FaceSetGroupGetListReq receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        PageInfo<GroupInfo> pageInfo = groupInfoService.getList(request);
        return ProtobufUtils.buildFacesetSendMessage(pageInfo.getList(), pageInfo.getSize());
    }

    /**
     * 对内&对外 删除用户组接口
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Validated FaceSetGroupDeleteReq receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        groupInfoService.delete(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }


}
