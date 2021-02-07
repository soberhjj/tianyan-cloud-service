package com.newland.tianyan.face.service;

import com.newland.face.message.NLFace;
import com.newland.tianyan.face.dto.face.BackendFacesetFaceCompareRequest;
import com.newland.tianyan.face.dto.face.BackendFacesetFaceDetectRequest;
import com.newland.tianyan.face.dto.face.BackendFacesetFaceSearchRequest;
import com.newland.tianyan.face.dto.face.FaceDetectVo;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public interface FaceSearchService {

    NLFace.CloudFaceSendMessage searchNew(BackendFacesetFaceSearchRequest request);

    NLFace.CloudFaceSendMessage compare(BackendFacesetFaceCompareRequest request);

    NLFace.CloudFaceSendMessage multiAttribute(FaceDetectVo vo);

    NLFace.CloudFaceSendMessage liveness(FaceDetectVo vo);

    NLFace.CloudFaceSendMessage detect(BackendFacesetFaceDetectRequest request);

    NLFace.CloudFaceSendMessage features(BackendFacesetFaceDetectRequest request, int model);
}
