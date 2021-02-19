package com.newland.tianyan.face.service;


import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.vo.FaceSetFaceCompareVo;
import com.newland.tianyan.face.vo.FaceSetFaceDetectVo;
import com.newland.tianyan.face.vo.FaceSetFaceSearchVo;
import com.newland.tianyan.face.vo.FaceDetectVo;
import newlandFace.NLFace;

/**
 * @Author: huangJunJie  2020-11-06 09:07
 */
public interface FacesetFaceService {

    NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchVo request);

    NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareVo request);

    NLFace.CloudFaceSendMessage multiAttribute(FaceDetectVo vo);

    NLFace.CloudFaceSendMessage liveness(FaceDetectVo vo);

    NLFace.CloudFaceSendMessage detect(FaceSetFaceDetectVo request);

    NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model);

}
