package com.newland.tianyan.face.service;


import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.dto.FaceSetFaceCompareReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceSearchReqDTO;
import com.newland.tianyan.face.domain.dto.FaceDetectReqDTO;
import newlandFace.NLFace;

/**
 * @Author: huangJunJie  2020-11-06 09:07
 */
public interface FacesetFaceService {

    NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchReqDTO request);

    NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReqDTO request);

    NLFace.CloudFaceSendMessage multiAttribute(FaceDetectReqDTO vo);

    NLFace.CloudFaceSendMessage liveness(FaceDetectReqDTO vo);

    NLFace.CloudFaceSendMessage detect(FaceSetFaceDetectReqDTO request);

    NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model);

}
