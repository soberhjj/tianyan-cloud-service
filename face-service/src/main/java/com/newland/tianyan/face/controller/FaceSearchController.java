package com.newland.tianyan.face.controller;

import com.newland.face.message.NLFace;
import com.newland.tianyan.face.dto.face.BackendFacesetFaceCompareRequest;
import com.newland.tianyan.face.dto.face.BackendFacesetFaceDetectRequest;
import com.newland.tianyan.face.dto.face.BackendFacesetFaceSearchRequest;
import com.newland.tianyan.face.dto.face.FaceDetectVo;
import com.newland.tianyan.face.service.FaceSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
@RestController
@RequestMapping("/face")
public class FaceSearchController {

    @Autowired
    private FaceSearchService faceSearchService;

    @PostMapping(value = "/search")
    public NLFace.CloudFaceSendMessage search(@RequestBody @Validated BackendFacesetFaceSearchRequest request) {
        return faceSearchService.searchNew(request);
    }

    @PostMapping(value = "/compare")
    public NLFace.CloudFaceSendMessage compare(@RequestBody @Validated BackendFacesetFaceCompareRequest request) {
        return faceSearchService.compare(request);
    }

    @RequestMapping(value = "/multiAttribute", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage multiAttribute(@RequestBody @Validated FaceDetectVo vo) {
        return faceSearchService.multiAttribute(vo);
    }

    @RequestMapping(value = "/liveness", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage liveness(@RequestBody @Validated FaceDetectVo vo) {
        return faceSearchService.liveness(vo);
    }

    @RequestMapping(value = "/detect", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage detect(@RequestBody @Validated BackendFacesetFaceDetectRequest request) {
        return faceSearchService.detect(request);
    }

    @RequestMapping(value = "/features/v20", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20old(@RequestBody @Validated BackendFacesetFaceDetectRequest request) {
        return faceSearchService.features(request, -20);
    }

    @RequestMapping(value = "/features/v20/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV20(@RequestBody @Validated BackendFacesetFaceDetectRequest request) {
        return faceSearchService.features(request, 20);
    }

    @RequestMapping(value = "/features/v36/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV36(@RequestBody @Validated BackendFacesetFaceDetectRequest request) {
        return faceSearchService.features(request, 36);
    }

    @RequestMapping(value = "/features/v34/new", method = RequestMethod.POST)
    public NLFace.CloudFaceSendMessage featuresV34(@RequestBody @Validated BackendFacesetFaceDetectRequest request) {
        return faceSearchService.features(request, 34);
    }
}
