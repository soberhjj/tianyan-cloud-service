package com.newland.tianyan.face.common.utils;

import com.newland.face.message.NLBackend;
import org.springframework.beans.BeanUtils;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
public class BeanConvertUtils {

    public static void copyProperties(Object source, String taskType, Object target) {
        NLBackend.BackendAllRequest request = ProtobufConvertUtils.toBackendAllRequest(source, taskType);
        Object sourceNew = ProtobufConvertUtils.parseTo(request, target.getClass());
        BeanUtils.copyProperties(sourceNew, target);
    }

}
