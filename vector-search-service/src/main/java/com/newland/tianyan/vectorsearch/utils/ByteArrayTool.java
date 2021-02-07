package com.newland.tianyan.vectorsearch.utils;

import java.io.*;

/**
 * @Author: huangJunJie  2021-02-05 10:17
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
