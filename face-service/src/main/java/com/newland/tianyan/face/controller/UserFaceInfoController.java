package com.newland.tianyan.face.controller;

import com.newland.face.message.NLBackend;
import com.newland.face.message.NLFace;
import com.newland.tianyan.common.exception.CommonException;
import com.newland.tianyan.common.utils.LogUtils;
import com.newland.tianyan.face.common.utils.ProtobufConvertUtils;
import com.newland.tianyan.face.domain.FaceInfo;
import com.newland.tianyan.face.dto.userFace.BackendFacesetFaceAddRequest;
import com.newland.tianyan.face.dto.userFace.BackendFacesetFaceDeleteRequest;
import com.newland.tianyan.face.dto.userFace.BackendFacesetUserFaceGetListRequest;
import com.newland.tianyan.face.service.UserFaceInfoService;
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
 * @author: RojiaHuang
 * @description: 人脸库用户（人脸相关）信息Controller
 * @date: 2021/2/4
 */
@RestController
@RequestMapping({"/face/faceset/user/face", "/backend/faceset/user/face"})
public class UserFaceInfoController {

    @Autowired
    private UserFaceInfoService userFaceInfoService;

    /**
     * 添加人脸
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage add(@RequestBody @Validated BackendFacesetFaceAddRequest receive) {
        FaceInfo face = userFaceInfoService.create(receive);
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
        List<FaceInfo> list = userFaceInfoService.getList(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage(list, list.size());
    }

    /**
     * 删除人脸
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Validated BackendFacesetFaceDeleteRequest receive) {
        userFaceInfoService.delete(receive);
        return ProtobufConvertUtils.buildFacesetSendMessage();
    }
}
