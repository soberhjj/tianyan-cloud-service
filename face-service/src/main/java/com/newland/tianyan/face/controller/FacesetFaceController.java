package com.newland.tianyan.face.controller;


import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceCompareRequest;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceDetectRequest;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceSearchRequest;
import com.newland.tianyan.face.privateBean.FaceDetectVo;
import com.newland.tianyan.face.service.FacesetFaceService;
import newlandFace.NLFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: huangJunJie  2020-11-06 11:04
 */
@RestController
@RequestMapping("/face")
public class FacesetFaceController {

    @Autowired
    FacesetFaceService facesetFaceService;

    @PostMapping(value = "/search")
    public NLFace.CloudFaceSendMessage search(@RequestBody @Validated BackendFacesetFaceSearchRequest request) {
        return facesetFaceService.searchNew(request);
    }

    @PostMapping(value = "/compare")
    public NLFace.CloudFaceSendMessage compare(@RequestBody @Validated BackendFacesetFaceCompareRequest request) {
        return facesetFaceService.compare(request);
    }

    @RequestMapping(value = "/multiAttribute", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage multiAttribute(@RequestBody @Validated FaceDetectVo vo) {
        return facesetFaceService.multiAttribute(vo);
    }

    @RequestMapping(value = "/liveness", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage liveness(@RequestBody @Validated FaceDetectVo vo) {
        return facesetFaceService.liveness(vo);
    }

    @RequestMapping(value = "/detect", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage detect(@RequestBody @Validated BackendFacesetFaceDetectRequest request) {
        return facesetFaceService.detect(request);
    }

    @RequestMapping(value = "/features/v20", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20old(@RequestBody  @Validated BackendFacesetFaceDetectRequest request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, -20);
    }

    @RequestMapping(value = "/features/v20/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20(@RequestBody  @Validated BackendFacesetFaceDetectRequest request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive,20);
    }

    @RequestMapping(value = "/features/v36/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV36(@RequestBody  @Validated BackendFacesetFaceDetectRequest request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive,36);
    }

    @RequestMapping(value = "/features/v34/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV34(@RequestBody  @Validated BackendFacesetFaceDetectRequest request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, 34);
    }
}
