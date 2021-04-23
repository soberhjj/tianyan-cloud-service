package com.newland.tianyan.face.controller;


import com.newland.tianya.commons.base.constants.TaskTypeEnums;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.dto.FaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceCompareReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceSearchReqDTO;
import com.newland.tianyan.face.service.FacesetFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: huangJunJie  2020-11-06 11:04
 */
@RestController
@RequestMapping({"/face", "/face/{version}"})
public class FacesetFaceController {

    @Autowired
    private FacesetFaceService facesetFaceService;

    @PostMapping(value = "/search")
    public NLFace.CloudFaceSendMessage search(@RequestBody @Valid FaceSetFaceSearchReqDTO request) {
        return facesetFaceService.searchNew(request);
    }

    @PostMapping(value = "/compare")
    public NLFace.CloudFaceSendMessage compare(@RequestBody @Valid FaceSetFaceCompareReqDTO request) {
        return facesetFaceService.compare(request);
    }

    @RequestMapping(value = "/multiAttribute", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage multiAttribute(@RequestBody @Valid FaceDetectReqDTO vo) {
        return facesetFaceService.multiAttribute(vo);
    }

    @RequestMapping(value = "/liveness", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage liveness(@RequestBody @Valid FaceDetectReqDTO vo) {
        return facesetFaceService.liveness(vo);
    }

    @RequestMapping(value = "/detect", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage detect(@RequestBody @Validated FaceDetectReqDTO request) {
        return facesetFaceService.detect(request);
    }

    @RequestMapping(value = "/features/v18/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV18(@RequestBody @Valid FaceSetFaceDetectReqDTO request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskTypeEnums.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, 18);
    }

    @RequestMapping(value = "/features/v20", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20old(@RequestBody @Valid FaceSetFaceDetectReqDTO request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskTypeEnums.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, -20);
    }

    @RequestMapping(value = "/features/v20/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20(@RequestBody @Valid FaceSetFaceDetectReqDTO request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskTypeEnums.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, 20);
    }

    @RequestMapping(value = "/features/v36/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV36(@RequestBody @Valid FaceSetFaceDetectReqDTO request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskTypeEnums.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, 36);
    }

    @RequestMapping(value = "/features/v34/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV34(@RequestBody @Valid FaceSetFaceDetectReqDTO request) {
        NLBackend.BackendAllRequest receive = ProtobufUtils.toBackendAllRequest(request, TaskTypeEnums.BACKEND_APP_GET_INFO);
        return facesetFaceService.features(receive, 34);
    }
}
