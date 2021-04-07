package com.newland.tianyan.face.service.impl;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.mq.IMqMessageService;
import com.newland.tianyan.face.service.IQualityCheckService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/2
 */
@Service
public class QualityCheckServiceImpl implements IQualityCheckService {

    @Autowired
    private IMqMessageService iMqMessageService;

    @Override
    public void checkQuality(int qualityControl, String image) {
        if (qualityControl == 0) {
            return;
        }
        Quality targetQuality;
        switch (qualityControl) {
            case 1:
                targetQuality = lowQuality;
                break;
            case 2:
                targetQuality = medianQuality;
                break;
            case 3:
                targetQuality = highQuality;
                break;
            default:
                throw ExceptionSupport.toException(GlobalExceptionEnum.ARGUMENT_INVALID_FORMAT, "qualityControl");
        }
        //eye distance
        NLFace.CloudFaceSendMessage.Builder detectBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        NLFace.CloudFaceSendMessage eyeDetect = iMqMessageService.amqpHelper(image, 1, 1);
        detectBuilder.mergeFrom(eyeDetect);
        NLFace.CloudFaceSendMessage detectRe = detectBuilder.build();

        Quality reqEyeQuality = new Quality(qualityControl, detectRe);
        reqEyeQuality.checkEyeDistance(targetQuality);
        //other quality
        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();
        NLFace.CloudFaceSendMessage otherDetect = iMqMessageService.amqpHelper(image, 1, 4);
        builder.mergeFrom(otherDetect);
        NLFace.CloudFaceSendMessage qualityRe = builder.build();

        Quality reqQuality = new Quality(qualityControl, qualityRe);
        reqQuality.check(targetQuality);
    }

    private final Quality lowQuality = new Quality(1, 20d, 40, 40, 40, 0.8f, 0.8f, 0f, 0.15f, 0.95f, 0.8f, 0.8f, 0.8f);

    private final Quality medianQuality = new Quality(2, 40d, 30, 30, 30, 0.6f, 0.6f, 0f, 0.2f, 0.9f, 0.6f, 0.6f, 0.6f);

    private final Quality highQuality = new Quality(3, 60d, 20, 20, 20, 0.4f, 0.4f, 0f, 0.3f, 0.8f, 0.4f, 0.4f, 0.4f);

    @AllArgsConstructor
    @Data
    public static class Quality {
        private int qualityControlLevel;
        private double eyeDistance;
        private int pitch;
        private int yaw;
        private int roll;
        private float blur;
        private float occlusion;
        private float brightness;
        private float minBrightness;
        private float maxBrightness;
        private float brightnessSideDiff;
        private float brightnessUpdownDiff;
        private float toneOffCenter;

        public Quality(int qualityControlLevel, NLFace.CloudFaceSendMessage source) {
            this.qualityControlLevel = qualityControlLevel;
            if (!CollectionUtils.isEmpty(source.getFaceInfosList())) {
                NLFace.CloudFaceDetectInfo detectInfo = source.getFaceInfos(0);
                this.eyeDistance = Math.sqrt(Math.pow(detectInfo.getPtx(0) - detectInfo.getPtx(1), 2)
                        + Math.pow(detectInfo.getPty(0) - detectInfo.getPty(1), 2));
            }
            if (!CollectionUtils.isEmpty(source.getFaceAttributesList())) {
                NLFace.CloudFaceAttributeResult faceAttributeResult = source.getFaceAttributes(0);
                this.pitch = faceAttributeResult.getPitch();
                this.yaw = faceAttributeResult.getYaw();
                this.roll = faceAttributeResult.getRoll();
                this.blur = faceAttributeResult.getBlur();
                this.occlusion = faceAttributeResult.getOcclusion();
                this.brightness = faceAttributeResult.getBrightness();
                this.brightnessSideDiff = faceAttributeResult.getBrightnessSideDiff();
                this.brightnessUpdownDiff = faceAttributeResult.getBrightnessUpdownDiff();
                this.toneOffCenter = faceAttributeResult.getToneOffCenter();
            }
        }

        public void checkEyeDistance(Quality target) {
            String level = getQualityControlLevelMsg();

            if (this.getEyeDistance() <= target.getEyeDistance()) {
                throw ExceptionSupport.toException(ExceptionEnum.EYE_DISTANCE_ERROR, level, target.getEyeDistance());
            }
        }

        public void check(Quality target) {
            String level = getQualityControlLevelMsg();
            if (this.getPitch() >= target.getPitch()) {
                throw ExceptionSupport.toException(ExceptionEnum.ABS_PITCH_ERROR, level, target.getPitch());
            }
            if (this.getYaw() >= target.getYaw()) {
                throw ExceptionSupport.toException(ExceptionEnum.ABS_YAW_ERROR, level, target.getYaw());
            }
            if (this.getRoll() >= target.getRoll()) {
                throw ExceptionSupport.toException(ExceptionEnum.ABS_ROLL_ERROR, level, target.getRoll());
            }
            if (this.getBlur() >= target.getBlur()) {
                throw ExceptionSupport.toException(ExceptionEnum.BLUR_ERROR, level, target.getBlur());
            }
            if (this.getOcclusion() >= target.getOcclusion()) {
                throw ExceptionSupport.toException(ExceptionEnum.OCCLUSION_ERROR, level, target.getOcclusion());
            }
            if (this.getBrightness() <= target.getMinBrightness() || this.getBrightness() >= target.getMaxBrightness()) {
                throw ExceptionSupport.toException(ExceptionEnum.BRIGHTNESS_ERROR, level, target.getMinBrightness(), target.getMaxBrightness());
            }
            if (this.getBrightnessSideDiff() >= target.getBrightnessSideDiff()) {
                throw ExceptionSupport.toException(ExceptionEnum.BRIGHTNESS_SIDE_DIFF_ERROR, level, target.getBrightnessSideDiff());
            }
            if (this.getBrightnessUpdownDiff() >= target.getBrightnessUpdownDiff()) {
                throw ExceptionSupport.toException(ExceptionEnum.BRIGHTNESS_UPDOWN_ERROR, level, target.getBrightnessUpdownDiff());
            }
            if (this.getToneOffCenter() >= target.getToneOffCenter()) {
                throw ExceptionSupport.toException(ExceptionEnum.TONE_OFF_CENTER_ERROR, level, target.getToneOffCenter());
            }
        }

        private String getQualityControlLevelMsg() {
            String level = "";
            switch (this.getQualityControlLevel()) {
                case 1:
                    level = "low";
                    break;
                case 2:
                    level = "median";
                    break;
                case 3:
                    level = "high";
                    break;
                default:
                    break;
            }
            return level;
        }
    }
}
