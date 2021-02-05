package com.newland.tianyan.common.utils;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public class IDUtils {
    /**
     * 随机id生成，使用雪花算法
     */
    public static long getRandomId() {
        SnowflakeIdWorker sf = new SnowflakeIdWorker();
        long id = sf.nextId();
        return id;
    }
}
