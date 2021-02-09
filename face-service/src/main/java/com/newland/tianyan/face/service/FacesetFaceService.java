package com.newland.tianyan.face.service;


import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceCompareRequest;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceDetectRequest;
import com.newland.tianyan.face.privateBean.BackendFacesetFaceSearchRequest;
import com.newland.tianyan.face.privateBean.FaceDetectVo;
import newlandFace.NLFace;

/**
 * @Author: huangJunJie  2020-11-06 09:07
 */
public interface FacesetFaceService {

    NLFace.CloudFaceSendMessage searchNew(BackendFacesetFaceSearchRequest request);

    NLFace.CloudFaceSendMessage compare(BackendFacesetFaceCompareRequest request);

    NLFace.CloudFaceSendMessage multiAttribute(FaceDetectVo vo);

    NLFace.CloudFaceSendMessage liveness(FaceDetectVo vo);

    NLFace.CloudFaceSendMessage detect(BackendFacesetFaceDetectRequest request);

    NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model);

}
