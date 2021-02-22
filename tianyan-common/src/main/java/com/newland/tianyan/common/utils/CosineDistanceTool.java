package com.newland.tianyan.common.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
public class CosineDistanceTool {

    public static float getConfidence(List<Float> rawFeature, byte[] faceFeature) {

        int size = 512;

        float[] rawFeatureArr = ArrayUtils.toPrimitive(rawFeature.toArray(new Float[0]), 0.0F);
        float[] normalizeFeatureArr = ByteArrayTool.convertByteToArray(faceFeature, size);

        return getDistance(rawFeatureArr, normalizeFeatureArr);
    }

    /**
     * @param var1 原生特征值
     * @param var2 归一化特征值
     * @return 余弦相似度
     */
    public static float getDistance(float[] var1, float[] var2) {
        float tsum = 0;
        float psum = 0;

        for (int i = 0; i < 512; i++) {
            tsum += var1[i] * var2[i];
            psum += var1[i] * var1[i];
        }

        double v = tsum / Math.sqrt(psum);
        v = (v + 1) / 2;
        return (float) v;
    }

    /**
     * @param var1 原生特征值
     * @param var2 原生特征值
     * @return 余弦相似度
     */
    public static float getNormalDistance(float[] var1, float[] var2) {
        float tsum = 0;
        float psum1 = 0;
        float psum2 = 0;

        for (int i = 0; i < 512; i++) {
            tsum += var1[i] * var2[i];
            psum1 += var1[i] * var1[i];
            psum2 += var2[i] * var2[i];
        }
        double v = tsum / (Math.sqrt(psum1) * Math.sqrt(psum2));
        v = (v + 1) / 2;
        return (float) v;
    }

    public static float getEurDistance(float[] var1, float[] var2) {
        
        float eur = 0;
        for (int i = 0; i < 512; i++) {
            eur += Math.pow(var1[i]-var2[i],2);
        }

        return (float) Math.sqrt(eur);
    }

    /**
     * @param eur 欧式距离
     * @return 余弦相似度[-1,1]
     */
    public static float convertEur2Cos(float eur) {
        return 1 - eur * eur / 2;
    }

    /**
     * @param eur 欧式距离
     * @return 余弦相似度[0, 1]
     */
    public static float convertEur2CosUp(float eur) {
        float cos = convertEur2Cos(eur);
        return (cos + 1) / 2;
    }

    public static void main(String[] args) {
        float eur = 0.69187295f;
        float cos = 1 - eur * eur / 2;
        System.out.println(cos);
    }
}
