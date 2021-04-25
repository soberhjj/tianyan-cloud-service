package com.newland.tianyan.face.controller;


import com.newland.tianya.commons.base.constants.TaskTypeEnums;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.model.proto.NLPage;
import com.newland.tianya.commons.base.utils.LogIdUtils;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceAddReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceDeleteReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetUserFaceGetListReqDTO;
import com.newland.tianyan.face.service.FacesetUserFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
@RequestMapping({"/face/faceset/user/face", "/backend/faceset/user/face","/face/{version}/faceset/user/face", "/backend/{version}/faceset/user/face"})
public class FacesetUserFaceController {

    @Autowired
    private FacesetUserFaceService facesetUserFaceService;

    /**
     * 添加人脸
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage add(@RequestBody @Valid FaceSetFaceAddReqDTO receive) throws Exception {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        FaceDO faceDO = facesetUserFaceService.create(request);
        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setLogId(LogIdUtils.traceId());
        if (faceDO!=null){
            result.setFaceId(faceDO.getId().toString());

            if (receive.getType() !=null && receive.getType() == 101) {
                ObjectInputStream in;
                float[] features = new float[512];
                try {
                    in = new ObjectInputStream(new ByteArrayInputStream(faceDO.getFeatures()));
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
                throw new BaseException(build.getErrorCode(), build.getErrorMsg());
            }
            return build;
        }
        return result.build();
    }

    /**
     * 获取人脸列表
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public NLPage.BackendFacesetPageMessage getList(@RequestBody @Valid FaceSetUserFaceGetListReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        List<FaceDO> list = facesetUserFaceService.getList(request);
        return ProtobufUtils.buildFacesetPageMessage(list, list.size());
    }

    /**
     * 删除人脸
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public NLBackend.BackendFacesetSendMessage delete(@RequestBody @Valid FaceSetFaceDeleteReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        facesetUserFaceService.delete(request);
        return ProtobufUtils.buildFacesetSendMessage();
    }


}
