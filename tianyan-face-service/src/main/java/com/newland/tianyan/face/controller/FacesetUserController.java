package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.constants.TaskTypeEnums;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLPage;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.dto.FaceSetUserCopyReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetUserDeleteReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetUserGetListReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetUserMessageReqDTO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.service.FacesetUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 人脸库用户信息Controller
 *
 * @Author: huangJunJie  2020-11-07 09:26
 */
@RestController
@RequestMapping({"/faceset/user", "/backend/faceset/user", "/face/faceset/user", "/backend/{version}/faceset/user", "/face/{version}/faceset/user"})
public class FacesetUserController {

    @Autowired
    private FacesetUserService facesetUserService;

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLPage.BackendFacesetPageMessage getList(@RequestBody @Valid FaceSetUserGetListReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        PageInfo<UserInfoDO> list = facesetUserService.getList(request);
        return ProtobufUtils.buildFacesetPageMessage(list.getList(), list.getSize());
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage copy(@RequestBody @Valid FaceSetUserCopyReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        facesetUserService.copy(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Valid FaceSetUserDeleteReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        facesetUserService.delete(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public NLBackend.BackendUserInfoMessage getInfo(@RequestBody @Valid FaceSetUserMessageReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        List<UserInfoDO> info = facesetUserService.getInfo(request);
        return ProtobufUtils.buildUserInfoMessage(info, info.size());
    }
}
