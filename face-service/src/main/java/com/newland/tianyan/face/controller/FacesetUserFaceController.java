package com.newland.tianyan.face.controller;


import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.LogUtils;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.Face;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceAddRequest;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceDeleteRequest;
import com.newland.tianyan.face.privateBean.BackendFacesetUserFaceGetListRequest;
import com.newland.tianyan.face.service.FacesetUserFaceService;
import newlandFace.NLFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * 人脸库用户（人脸相关）信息Controller
 *
 * @Author: huangJunJie  2020-11-04 09:13
 */
@RestController
@RequestMapping({"/face/faceset/user/face", "/backend/faceset/user/face"})
public class FacesetUserFaceController {

    @Autowired
    private FacesetUserFaceService facesetUserFaceService;

    /**
     * 添加人脸
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage add(@RequestBody @Validated BackendFacesetFaceAddRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        Face face = facesetUserFaceService.create(request);
        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setLogId(LogUtils.getLogId());
        result.setFaceId(face.getId().toString());

        if (receive.getType() == 101) {
            ObjectInputStream in;
            float[] features = new float[512];
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(face.getFeatures()));
                features = (float[]) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            NLFace.CloudFaceFeatureResult.Builder builder = result.addFeatureResultBuilder();
            for (int i = 0; i < 512; i++) {
                builder.addFeatures(features[i]);
            }
        }
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            throw new CommonException(build.getErrorCode(), build.getErrorMsg());
        }
        return build;
    }

    /**
     * 获取人脸列表
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage getList(@RequestBody @Validated BackendFacesetUserFaceGetListRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        List<Face> list = facesetUserFaceService.getList(request);
        return ProtobufUtils.buildFacesetSendMessage(list, list.size());
    }

    /**
     * 删除人脸
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Validated BackendFacesetFaceDeleteRequest receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        facesetUserFaceService.delete(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }


}
