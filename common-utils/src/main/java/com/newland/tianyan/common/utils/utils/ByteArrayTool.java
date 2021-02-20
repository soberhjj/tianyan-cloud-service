package com.newland.tianyan.common.utils.utils;

import java.io.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/20
 */
public class ByteArrayTool {

    public static float[] convertByteToArray(byte[] source, int size) {
        float[] target = new float[size];
        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(new ByteArrayInputStream(source));
            target = (float[]) inputStream.readObject();
            inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return target;
    }

    public static byte[] convertArrayToByte(float[] source, int size) {
        byte[] target = new byte[size];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(source);
            target = out.toByteArray();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target;
    }
}
