package com.newland.tianyan.face.mq;

/**
 * @author: RojiaHuang
 * @description: 统一维护消息队列名
 * @date: 2021/1/22
 */
public class RabbitMqQueueName {

    /**
     * 人脸检测队列
     */
    public static String FACE_DETECT_QUEUE = "faceDetectQueue";

    public static String FACE_DETECT_QUEUE_V18 = "faceDetectQueue_V18";

    public static String FACE_DETECT_QUEUE_V20 = "faceDetectQueue_V20";

    public static String FACE_DETECT_QUEUE_V20_OLD = "faceDetectQueue_V20_old";

    public static String FACE_DETECT_QUEUE_V34 = "faceDetectQueue_V34";

    public static String FACE_DETECT_QUEUE_V36 = "faceDetectQueue_V36";
    /**
     * 人脸feature队列
     */
    public static String FACE_FEATURE_QUEUE = "faceFeatureQueue";

    public static String FACE_FEATURE_QUEUE_V18 = "faceFeatureQueue_V18";

    public static String FACE_FEATURE_QUEUE_V20 = "faceFeatureQueue_V20";

    public static String FACE_FEATURE_QUEUE_V20_OLD = "faceFeatureQueue_V20_old";

    public static String FACE_FEATURE_QUEUE_V34 = "faceFeatureQueue_V34";

    public static String FACE_FEATURE_QUEUE_V36 = "faceFeatureQueue_V36";
}
