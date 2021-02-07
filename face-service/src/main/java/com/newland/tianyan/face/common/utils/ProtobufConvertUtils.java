package com.newland.tianyan.face.common.utils;

import com.alibaba.fastjson.JSON;
import com.googlecode.protobuf.format.JsonFormat;
import com.newland.face.message.NLBackend;
import com.newland.tianyan.common.utils.LogUtils;

import java.util.List;

public class ProtobufConvertUtils extends ProtobufBaseUtils{
    public static NLBackend.BackendFacesetSendMessage buildFacesetSendMessage() {
        NLBackend.BackendFacesetSendMessage.Builder builder = NLBackend.BackendFacesetSendMessage.newBuilder();
        builder.setLogId(LogUtils.getLogId());
        return builder.build();
    }

    public static NLBackend.BackendFacesetSendMessage buildFacesetSendMessage(List results, long count) {
        NLBackend.BackendFacesetSendMessage.Builder builder = NLBackend.BackendFacesetSendMessage.newBuilder();
        builder.setLogId(LogUtils.getLogId());
        builder.setCount((int) count);

        NLBackend.BackendFacesetSendMessage.BackendFacesetTableMessage.Builder tableBuilder =
                NLBackend.BackendFacesetSendMessage.BackendFacesetTableMessage.newBuilder();

        for (Object result : results) {
            String jsonFormat = JSON.toJSONString(result, getConfig());
            try {
                JsonFormat.merge(jsonFormat, tableBuilder);
                builder.addResult(tableBuilder);
            } catch (JsonFormat.ParseException e) {
                e.printStackTrace();
            }
        }
        return builder.build();
    }

    public static NLBackend.BackendAppSendMessage buildAppSendMessage() {
        NLBackend.BackendAppSendMessage.Builder builder = NLBackend.BackendAppSendMessage.newBuilder();
        builder.setLogId(LogUtils.getLogId());
        return builder.build();
    }

    public static NLBackend.BackendAppSendMessage buildAppSendMessage(List results, long count) {
        NLBackend.BackendAppSendMessage.Builder builder = NLBackend.BackendAppSendMessage.newBuilder();
        builder.setLogId(LogUtils.getLogId());
        builder.setCount((int) count);

        NLBackend.BackendAppSendMessage.BackendAppTableMessage.Builder tableBuilder = NLBackend.BackendAppSendMessage.BackendAppTableMessage.newBuilder();

        for (Object result : results) {
            String jsonFormat = JSON.toJSONString(result, getConfig());
            try {
                JsonFormat.merge(jsonFormat, tableBuilder);
                builder.addResult(tableBuilder);
            } catch (JsonFormat.ParseException e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }
}
