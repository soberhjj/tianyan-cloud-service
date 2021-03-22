package com.newland.tianya.commons.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.newland.tianyan.common.utils.message.NLBackend;
import org.springframework.data.domain.Page;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ProtobufUtils {

    private static SerializeConfig config = null;
    private static final Map<Class<?>, Method> METHOD_CACHE = new ConcurrentReferenceHashMap<>();

    private static SerializeConfig getConfig() {
        if (config == null) {
            SerializeConfig serializeConfig = new SerializeConfig();
            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
            config = serializeConfig;
        }
        return config;
    }

    public static <T extends Message> T buildMessage(Class<T> clazz) {
        return buildMessage(clazz, LogUtils.traceId(), null);
    }

    public static <T extends Message> T buildMessage(Class<T> clazz, Object value) {
        return buildMessage(clazz, LogUtils.traceId(), value);
    }

    private static <T extends Message> T buildMessage(Class<T> clazz, String logId, Object value) {
        try {
            Message.Builder target = getMessageBuilder(clazz);
            return buildMessage(target, toValueMap(logId, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Message.Builder getMessageBuilder(Class<? extends Message> clazz) throws Exception {
        try {
            Method method = METHOD_CACHE.get(clazz);
            if (method == null) {
                method = clazz.getMethod("newBuilder");
                METHOD_CACHE.put(clazz, method);
            }
            return (Message.Builder) method.invoke(clazz);
        } catch (Exception ex) {
            throw new Exception("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Message> T buildMessage(Message.Builder builder, Map<String, Object> fields) {
        Descriptors.Descriptor descriptor = builder.getDescriptorForType();

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            Descriptors.FieldDescriptor fieldDescriptor = getField(descriptor, entry.getKey());
            if (fieldDescriptor == null) {
                continue;
            }

            if (fieldDescriptor.isRepeated() && fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                if (entry.getValue() instanceof Map) {
                    Message message = buildMessage(builder.newBuilderForField(fieldDescriptor), (Map<String, Object>) entry.getValue());
                    builder.addRepeatedField(fieldDescriptor, message);
                } else if (entry.getValue() instanceof List) {
                    for (Object object : (List) entry.getValue()) {
                        Message message = buildMessage(builder.newBuilderForField(fieldDescriptor), (Map<String, Object>) object);
                        builder.addRepeatedField(fieldDescriptor, message);
                    }
                }
            } else {
                builder.setField(fieldDescriptor, buildValue(builder, fieldDescriptor, entry.getValue()));
            }
        }
        return (T) builder.build();
    }

    private static Map<String, Object> toValueMap(String logId, Object value) {
        Map<String, Object> message = new HashMap<>();
        message.put("log_id", logId);

        Object result;

        if (value instanceof Page) {
            message.put("count", ((Page) value).getTotalElements());
            List<Map> list = new LinkedList<>();
            for (Object item : ((Page) value).getContent()) {
                list.add(JSON.parseObject(JSON.toJSONString(item)));
            }
            result = list;
        } else if (value instanceof Collection) {
            message.put("count", ((Collection) value).size());
            List<Map> list = new LinkedList<>();
            for (Object item : (Collection) value) {
                list.add(JSON.parseObject(JSON.toJSONString(item)));
            }
            result = list;
        } else {
            result = JSON.parseObject(JSON.toJSONString(value));
        }

        message.put("result", result);
        return message;
    }

    @SuppressWarnings("unchecked")
    private static Object buildValue(Message.Builder parent, Descriptors.FieldDescriptor field, Object value) {
        if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            Message.Builder fieldBuilder = parent.newBuilderForField(field);
            return buildMessage(fieldBuilder, (Map<String, Object>) value);
        } else if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            return field.getEnumType().findValueByName((String) value);
        } else {
            switch (field.getJavaType()) {
                // float is a special case
                case FLOAT:
                    return Float.valueOf(value.toString());
                case INT:
                    return Integer.valueOf(value.toString());
                case LONG:
                    return Long.valueOf(value.toString());
                case DOUBLE:
                    return Double.valueOf(value.toString());
                default:
                    return value.toString();
            }
        }
    }

    private static Descriptors.FieldDescriptor getField(Descriptors.Descriptor descriptor, String name) {
        return descriptor.findFieldByName(name);
    }

    public static NLBackend.BackendAllRequest toBackendAllRequest(Object obj, String taskType) {
        NLBackend.BackendAllRequest.Builder builder = NLBackend.BackendAllRequest.newBuilder();
        String jsonFormat = JSON.toJSONString(obj, getConfig());
        try {
            JsonFormat.merge(jsonFormat, builder);
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
        }
        builder.setTaskType(taskType);
        builder.setLogId(LogUtils.traceId());
        return builder.build();
    }

    public static <T> T parseTo(NLBackend.BackendAllRequest request, Class<T> clazz) {
        String jsonString = JsonFormat.printToString(request);
        T result = JSON.parseObject(jsonString, clazz);
        for (Field field : clazz.getDeclaredFields()) {
            if ("startIndex".equals(field.getName())) {
                try {
                    Field startIndexField = clazz.getDeclaredField("startIndex");
                    Field lengthField = clazz.getDeclaredField("length");

                    startIndexField.setAccessible(true);
                    lengthField.setAccessible(true);

                    int startIndex = startIndexField.getInt(result);
                    int length = lengthField.getInt(result);

                    if (length == 0) {
                        length = 100;
                    }
                    int pageNum = startIndex / length + 1;
                    startIndexField.setInt(result, pageNum);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static NLBackend.BackendErrorMessage buildErrorMessage(String lodId, int errorCode, String errorMsg) {
        NLBackend.BackendErrorMessage.Builder builder = NLBackend.BackendErrorMessage.newBuilder();
        builder.setLogId(lodId);
        builder.setErrorCode(errorCode);
        builder.setErrorMsg(errorMsg);
        return builder.build();
    }

    public static NLBackend.BackendLoginSendMessage buildLoginSendMessage() {
        NLBackend.BackendLoginSendMessage.Builder builder = NLBackend.BackendLoginSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
        return builder.build();
    }

    public static NLBackend.BackendLoginSendMessage buildLoginSendMessage(String account, String mailbox) {
        NLBackend.BackendLoginSendMessage.Builder builder = NLBackend.BackendLoginSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
        builder.setMailbox(mailbox);
        builder.setAccount(account);
        return builder.build();
    }

    public static NLBackend.BackendAppSendMessage buildAppSendMessage() {
        NLBackend.BackendAppSendMessage.Builder builder = NLBackend.BackendAppSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
        return builder.build();
    }

    public static NLBackend.BackendAppSendMessage buildAppSendMessage(List results, long count) {
        NLBackend.BackendAppSendMessage.Builder builder = NLBackend.BackendAppSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
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

    public static NLBackend.BackendMailSendMessage buildMailSendMessage(List results, long count) {
        NLBackend.BackendMailSendMessage.Builder builder = NLBackend.BackendMailSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
        builder.setCount((int) count);

        NLBackend.BackendMailSendMessage.BackendMailDetailMessage.Builder tableBuilder = NLBackend.BackendMailSendMessage.BackendMailDetailMessage.newBuilder();

        for (Object result : results) {
            String jsonFormat = JSON.toJSONString(result, getConfig());
            try {
                JsonFormat.merge(jsonFormat, tableBuilder);
                builder.addMailList(tableBuilder);
            } catch (JsonFormat.ParseException e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }

    public static NLBackend.BackendFacesetSendMessage buildFacesetSendMessage() {
        NLBackend.BackendFacesetSendMessage.Builder builder = NLBackend.BackendFacesetSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
        return builder.build();
    }

    public static NLBackend.BackendFacesetSendMessage buildFacesetSendMessage(List results, long count) {
        return buildFacesetSendMessage(LogUtils.traceId(), results, count);
    }

    public static NLBackend.BackendFacesetSendMessage buildFacesetSendMessage(String faceId) {
        NLBackend.BackendFacesetSendMessage.Builder builder = NLBackend.BackendFacesetSendMessage.newBuilder();
        builder.setLogId(LogUtils.traceId());
        builder.setFaceId(faceId);
        return builder.build();
    }


    public static NLBackend.BackendFacesetSendMessage buildFacesetSendMessage(String logId, List results, long count) {
        NLBackend.BackendFacesetSendMessage.Builder builder = NLBackend.BackendFacesetSendMessage.newBuilder();
        builder.setLogId(logId);
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

    public static String printToString(Message message) {
        return JsonFormat.printToString(message);
    }

    public static String printAppMessageToString(byte[] data) throws InvalidProtocolBufferException {
        try {
            NLBackend.BackendAppSendMessage message = NLBackend.BackendAppSendMessage.parseFrom(data);
            return printToString(message);
        } catch (InvalidProtocolBufferException e) {
            NLBackend.BackendErrorMessage message = NLBackend.BackendErrorMessage.parseFrom(data);
            return printToString(message);
        }
    }

    public static String printFacesetMessageToString(byte[] data) throws InvalidProtocolBufferException {
        try {
            NLBackend.BackendFacesetSendMessage message = NLBackend.BackendFacesetSendMessage.parseFrom(data);
            return printToString(message);
        } catch (InvalidProtocolBufferException e) {
            NLBackend.BackendErrorMessage message = NLBackend.BackendErrorMessage.parseFrom(data);
            return printToString(message);
        }
    }

    public static String printLoginMessageToString(byte[] data) throws InvalidProtocolBufferException {
        try {
            NLBackend.BackendLoginSendMessage message = NLBackend.BackendLoginSendMessage.parseFrom(data);
            return printToString(message);
        } catch (InvalidProtocolBufferException e) {
            NLBackend.BackendErrorMessage message = NLBackend.BackendErrorMessage.parseFrom(data);
            return printToString(message);
        }
    }
}