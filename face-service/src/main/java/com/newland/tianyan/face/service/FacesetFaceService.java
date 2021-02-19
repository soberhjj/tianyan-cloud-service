package com.newland.tianyan.face.service;


import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.vo.FaceSetFaceCompareReq;
import com.newland.tianyan.face.vo.FaceSetFaceDetectReq;
import com.newland.tianyan.face.vo.FaceSetFaceSearchReq;
import com.newland.tianyan.face.vo.FaceDetectReq;
import newlandFace.NLFace;

/**
 * @Author: huangJunJie  2020-11-06 09:07
 */
public interface FacesetFaceService {

    NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchReq request);

    NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReq request);

    NLFace.CloudFaceSendMessage multiAttribute(FaceDetectReq vo);

    NLFace.CloudFaceSendMessage liveness(FaceDetectReq vo);

    NLFace.CloudFaceSendMessage detect(FaceSetFaceDetectReq request);

    NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model);

}
