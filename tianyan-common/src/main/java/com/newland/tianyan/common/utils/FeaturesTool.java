package com.newland.tianyan.common.utils;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/20
 */
public class FeaturesTool {
    private static final int SIZE = 512;

    /**
     * 归一化并转字节数组
     *
     * @param rawFeature 由算法计算的原生特征值
     * @return face对象存储的特征值
     */
    public static byte[] normalizeConvertToByte(List<Float> rawFeature) {
        float[] normalFeature = normalize(rawFeature);
        return ByteArrayTool.convertArrayToByte(normalFeature, SIZE);
    }

    /**
     * 归一化处理
     *
     * @param rawFeature 由算法计算的原生特征值
     * @return miivus向量值
     */
    public static List<Float> normalizeConvertToList(List<Float> rawFeature) {
        float[] normalFeature = normalize(rawFeature);
        return convertArrayToList(normalFeature);
    }

    /**
     * 归一化处理
     *
     * @param rawFeature 由算法计算的原生特征值
     * @return 原生特征值计算的处理值
     */
    public static float[] normalize(List<Float> rawFeature) {
        if (CollectionUtils.isEmpty(rawFeature)) {
            return null;
        }
        int size = rawFeature.size();
        float squareSum = 0;
        for (Float featureItem : rawFeature) {
            squareSum += featureItem * featureItem;
        }

        float[] normalFeature = new float[size];
        for (int i = 0; i < size; i++) {
            normalFeature[i] = rawFeature.get(i) / (float) Math.sqrt(squareSum);
        }
        return normalFeature;
    }

    public static float[] normalize(float[] rawFeature){
        double v = 0;
        for (int i = 0; i < 512; i++) {
            double temp = rawFeature[i];
            v += temp * temp;
        }
        double sqrtV = Math.sqrt(v);

        float[] afterNormalize = new float[512];
        for (int i = 0; i < 512; i++) {
            double temp = rawFeature[i] / sqrtV;
            afterNormalize[i] = Float.parseFloat(String.valueOf(temp));
        }
        return afterNormalize;
    }
    /**
     * @param source face对象存储的特征值
     * @return 原生特征值计算的处理值list
     */
    public static List<Float> convertByteArrayToList(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        float[] result = ByteArrayTool.convertByteToArray(source, SIZE);
        return convertArrayToList(result);
    }

    /**
     * @param floats 原生特征值计算的处理值Array
     * @return 原生特征值计算的处理值list
     */
    public static List<Float> convertArrayToList(float[] floats) {
        if (floats == null || floats.length == 0) {
            return null;
        }
        int size = floats.length;
        List<Float> features = new ArrayList<>(size);
        for (float featureItem : floats) {
            features.add(featureItem);
        }
        return features;
    }

    public static float[] convertToArr(List<Float> floatList) {
        if (CollectionUtils.isEmpty(floatList)) {
            return null;
        }
        float[] target = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            target[i] = floatList.get(i);
        }
        return target;
    }

    public static Boolean compareVector(float[] source, float[] target) {
        if (source == null || target == null) {
            return false;
        }
        if (source.length != target.length) {
            return false;
        }
        boolean check = true;
        for (int i = 0; i < source.length; i++) {
            if (source[i] != target[i]) {
                check = false;
                break;
            }
        }
        return check;
    }
}
