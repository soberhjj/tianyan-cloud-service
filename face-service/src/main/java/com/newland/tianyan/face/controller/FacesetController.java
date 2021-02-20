package com.newland.tianyan.face.controller;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.entity.AppInfo;
import com.newland.tianyan.face.vo.FaceSetGetListReq;
import com.newland.tianyan.face.service.FacesetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *人脸库应用信息Controller
 * @Author: huangJunJie  2020-11-06 14:17
 */
@RestController
@RequestMapping({"/faceset","/backend/faceset"})
public class FacesetController {

    @Autowired
    private FacesetService facesetService;

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage getList(@RequestBody @Validated FaceSetGetListReq receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        PageInfo<AppInfo> pageInfo = facesetService.getList(request);
        return ProtobufUtils.buildFacesetSendMessage(pageInfo.getList(), pageInfo.getSize());
    }
}
