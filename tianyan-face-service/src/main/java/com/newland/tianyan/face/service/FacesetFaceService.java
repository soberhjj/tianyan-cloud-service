package com.newland.tianyan.face.service;


import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianyan.face.domain.dto.*;

import java.io.IOException;

/**
 * @Author: huangJunJie  2020-11-06 09:07
 */
public interface FacesetFaceService {

    NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchReqDTO request);

    NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReqDTO request);

    NLFace.CloudFaceSendMessage multiAttribute(FaceMultiAttributeReqDTO vo);

    NLFace.CloudFaceSendMessage liveness(FaceLiveNessReqDTO vo);

    NLFace.CloudFaceSendMessage detect(FaceDetectReqDTO vo);

    NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model);

    NLFace.CloudFaceSendMessage interactLiveness(FaceInteractLiveNessReqDTO request);

}
