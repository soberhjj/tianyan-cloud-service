package com.newland.tianyan.face.controller;


import com.newland.tianyan.common.constans.TaskType;
import com.newland.tianyan.common.utils.ImgFormatConvertUtils;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.dto.FaceSetFaceCompareReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceSearchReqDTO;
import com.newland.tianyan.face.domain.dto.FaceDetectReqDTO;
import com.newland.tianyan.face.service.FacesetFaceService;
import newlandFace.NLFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Author: huangJunJie  2020-11-06 11:04
 */
@RestController
@RequestMapping({"/face","/face/{version}"})
public class FacesetFaceController {

    @Autowired
    FacesetFaceService facesetFaceService;

    @PostMapping(value = "/search")
    public NLFace.CloudFaceSendMessage search(@RequestBody @Validated FaceSetFaceSearchReqDTO request){
        return facesetFaceService.searchNew(request);
    }

    @PostMapping(value = "/compare")
    public NLFace.CloudFaceSendMessage compare(@RequestBody @Validated FaceSetFaceCompareReqDTO request) throws IOException {
        return facesetFaceService.compare(request);
    }

    @RequestMapping(value = "/multiAttribute", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage multiAttribute(@RequestBody @Validated FaceDetectReqDTO vo) throws IOException {
        return facesetFaceService.multiAttribute(vo);
    }

    @RequestMapping(value = "/liveness", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage liveness(@RequestBody @Validated FaceDetectReqDTO vo) throws IOException {
        return facesetFaceService.liveness(vo);
    }

    @RequestMapping(value = "/detect", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage detect(@RequestBody @Validated FaceSetFaceDetectReqDTO request) throws IOException {
        return facesetFaceService.detect(request);
    }

    @RequestMapping(value = "/features/v20", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20old(@RequestBody  @Validated FaceSetFaceDetectReqDTO request) throws IOException {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, -20);
    }

    @RequestMapping(value = "/features/v20/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20(@RequestBody  @Validated FaceSetFaceDetectReqDTO request) throws IOException {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive,20);
    }

    @RequestMapping(value = "/features/v36/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV36(@RequestBody  @Validated FaceSetFaceDetectReqDTO request) throws IOException {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive,36);
    }

    @RequestMapping(value = "/features/v34/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV34(@RequestBody  @Validated FaceSetFaceDetectReqDTO request) throws IOException {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskType.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, 34);
    }
}
