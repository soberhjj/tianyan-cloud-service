//package com.newland.tianyan.common.utils;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.PropertyNamingStrategy;
//import com.alibaba.fastjson.serializer.SerializeConfig;
//import com.google.protobuf.Descriptors;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.google.protobuf.Message;
//import com.googlecode.protobuf.format.JsonFormat;
//import com.newland.tianyan.common.utils.message.NLBackend.*;
//import org.springframework.data.domain.Page;
//import org.springframework.util.ConcurrentReferenceHashMap;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.*;
//
//public class ProtobufUtils {
//
//    private static SerializeConfig config = null;
//    private static final Map<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap<>();
//
//    private static SerializeConfig getConfig() {
//        if (config == null) {
//            SerializeConfig serializeConfig = new SerializeConfig();
//            serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
//            config = serializeConfig;
//        }
//        return config;
//    }
//
//    public static <T extends Message> T buildMessage(Class<T> clazz) {
//        return buildMessage(clazz, LogUtils.getLogId(), null);
//    }
//
//    public static <T extends Message> T buildMessage(Class<T> clazz, Object value) {
//        return buildMessage(clazz, LogUtils.getLogId(), value);
//    }
//
//    private static <T extends Message> T buildMessage(Class<T> clazz, String logId, Object value) {
//        try {
//            Message.Builder target = getMessageBuilder(clazz);
//            return buildMessage(target, toValueMap(logId, value));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static Message.Builder getMessageBuilder(Class<? extends Message> clazz) throws Exception {
//        try {
//            Method method = methodCache.get(clazz);
//            if (method == null) {
//                method = clazz.getMethod("newBuilder");
//                methodCache.put(clazz, method);
//            }
//            return (Message.Builder) method.invoke(clazz);
//        } catch (Exception ex) {
//            throw new Exception("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private static <T extends Message> T buildMessage(Message.Builder builder, Map<String, Object> fields) {
//        Descriptors.Descriptor descriptor = builder.getDescriptorForType();
//
//        for (Map.Entry<String, Object> entry : fields.entrySet()) {
//            if (entry.getValue() == null) {
//                continue;
//            }
//
//            Descriptors.FieldDescriptor fieldDescriptor = getField(descriptor, entry.getKey());
//            if (fieldDescriptor == null) {
//                continue;
//            }
//
//            if (fieldDescriptor.isRepeated() && fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
//                if (entry.getValue() instanceof Map) {
//                    Message message = buildMessage(builder.newBuilderForField(fieldDescriptor), (Map<String, Object>) entry.getValue());
//                    builder.addRepeatedField(fieldDescriptor, message);
//                } else if (entry.getValue() instanceof List) {
//                    for (Object object : (List) entry.getValue()) {
//                        Message message = buildMessage(builder.newBuilderForField(fieldDescriptor), (Map<String, Object>) object);
//                        builder.addRepeatedField(fieldDescriptor, message);
//                    }
//                }
//            } else {
//                builder.setField(fieldDescriptor, buildValue(builder, fieldDescriptor, entry.getValue()));
//            }
//        }
//        return (T) builder.build();
//    }
//
//    private static Map<String, Object> toValueMap(String logId, Object value) {
//        Map<String, Object> message = new HashMap<>();
//        message.put("log_id", logId);
//
//        Object result;
//
//        if (value instanceof Page) {
//            message.put("count", ((Page) value).getTotalElements());
//            List<Map> list = new LinkedList<>();
//            for (Object item : ((Page) value).getContent()) {
//                list.add(JSON.parseObject(JSON.toJSONString(item)));
//            }
//            result = list;
//        } else if (value instanceof Collection) {
//            message.put("count", ((Collection) value).size());
//            List<Map> list = new LinkedList<>();
//            for (Object item : (Collection) value) {
//                list.add(JSON.parseObject(JSON.toJSONString(item)));
//            }
//            result = list;
//        } else {
//            result = JSON.parseObject(JSON.toJSONString(value));
//        }
//
//        message.put("result", result);
//        return message;
//    }
//
//    @SuppressWarnings("unchecked")
//    private static Object buildValue(Message.Builder parent, Descriptors.FieldDescriptor field, Object value) {
//        if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
//            Message.Builder fieldBuilder = parent.newBuilderForField(field);
//            return buildMessage(fieldBuilder, (Map<String, Object>) value);
//        } else if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
//            return field.getEnumType().findValueByName((String) value);
//        } else {
//            switch (field.getJavaType()) {
//                case FLOAT: // float is a special case
//                    return Float.valueOf(value.toString());
//                case INT:
//                    return Integer.valueOf(value.toString());
//                case LONG:
//                    return Long.valueOf(value.toString());
//                case DOUBLE:
//                    return Double.valueOf(value.toString());
//                default:
//                    return value.toString();
//            }
//        }
//    }
//
//    private static Descriptors.FieldDescriptor getField(Descriptors.Descriptor descriptor, String name) {
//        return descriptor.findFieldByName(name);
//    }
//
//    public static BackendAllRequest toBackendAllRequest(Object obj, String taskType) {
//        BackendAllRequest.Builder builder = BackendAllRequest.newBuilder();
//        String jsonFormat = JSON.toJSONString(obj, getConfig());
//        try {
//            JsonFormat.merge(jsonFormat, builder);
//        } catch (JsonFormat.ParseException e) {
//            e.printStackTrace();
//        }
//        builder.setTaskType(taskType);
//        builder.setLogId(LogUtils.getLogId());
//        return builder.build();
//    }
//
//    public static <T> T parseTo(BackendAllRequest request, Class<T> clazz) {
//        String jsonString = JsonFormat.printToString(request);
//        T result = JSON.parseObject(jsonString, clazz);
//        for (Field field : clazz.getDeclaredFields()) {
//            if ("startIndex".equals(field.getName())) {
//                try {
//                    Field startIndexField = clazz.getDeclaredField("startIndex");
//                    Field lengthField = clazz.getDeclaredField("length");
//
//                    startIndexField.setAccessible(true);
//                    lengthField.setAccessible(true);
//
//                    int startIndex = startIndexField.getInt(result);
//                    int length = lengthField.getInt(result);
//
//                    if (length == 0) {
//                        length = 100;
//                    }
//                    int pageNum = startIndex / length + 1;
//                    startIndexField.setInt(result, pageNum);
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }
//
//    public static BackendErrorMessage buildErrorMessage(String lodId, int error_code, String error_msg) {
//        BackendErrorMessage.Builder builder = BackendErrorMessage.newBuilder();
//        builder.setLogId(lodId);
//        builder.setErrorCode(error_code);
//        builder.setErrorMsg(error_msg);
//        return builder.build();
//    }
//
//    public static BackendLoginSendMessage buildLoginSendMessage() {
//        BackendLoginSendMessage.Builder builder = BackendLoginSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        return builder.build();
//    }
//
//    public static BackendLoginSendMessage buildLoginSendMessage(String account, String mailbox) {
//        BackendLoginSendMessage.Builder builder = BackendLoginSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        builder.setMailbox(mailbox);
//        builder.setAccount(account);
//        return builder.build();
//    }
//
//    public static BackendAppSendMessage buildAppSendMessage() {
//        BackendAppSendMessage.Builder builder = BackendAppSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        return builder.build();
//    }
//
//    public static BackendAppSendMessage buildAppSendMessage(List results, long count) {
//        BackendAppSendMessage.Builder builder = BackendAppSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        builder.setCount((int) count);
//
//        BackendAppSendMessage.BackendAppTableMessage.Builder tableBuilder = BackendAppSendMessage.BackendAppTableMessage.newBuilder();
//
//        for (Object result : results) {
//            String jsonFormat = JSON.toJSONString(result, getConfig());
//            try {
//                JsonFormat.merge(jsonFormat, tableBuilder);
//                builder.addResult(tableBuilder);
//            } catch (JsonFormat.ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return builder.build();
//    }
//
//    public static BackendMailSendMessage buildMailSendMessage(List results, long count) {
//        BackendMailSendMessage.Builder builder = BackendMailSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        builder.setCount((int) count);
//
//        BackendMailSendMessage.BackendMailDetailMessage.Builder tableBuilder = BackendMailSendMessage.BackendMailDetailMessage.newBuilder();
//
//        for (Object result : results) {
//            String jsonFormat = JSON.toJSONString(result, getConfig());
//            try {
//                JsonFormat.merge(jsonFormat, tableBuilder);
//                builder.addMailList(tableBuilder);
//            } catch (JsonFormat.ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return builder.build();
//    }
//
//    public static BackendFacesetSendMessage buildFacesetSendMessage() {
//        BackendFacesetSendMessage.Builder builder = BackendFacesetSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        return builder.build();
//    }
//
//    public static BackendFacesetSendMessage buildFacesetSendMessage(List results, long count) {
//        return buildFacesetSendMessage(LogUtils.getLogId(), results, count);
//    }
//
//    public static BackendFacesetSendMessage buildFacesetSendMessage(String face_id) {
//        BackendFacesetSendMessage.Builder builder = BackendFacesetSendMessage.newBuilder();
//        builder.setLogId(LogUtils.getLogId());
//        builder.setFaceId(face_id);
//        return builder.build();
//    }
//
//
//    public static BackendFacesetSendMessage buildFacesetSendMessage(String logId, List results, long count) {
//        BackendFacesetSendMessage.Builder builder = BackendFacesetSendMessage.newBuilder();
//        builder.setLogId(logId);
//        builder.setCount((int) count);
//
//        BackendFacesetSendMessage.BackendFacesetTableMessage.Builder tableBuilder =
//                BackendFacesetSendMessage.BackendFacesetTableMessage.newBuilder();
//
//        for (Object result : results) {
//            String jsonFormat = JSON.toJSONString(result, getConfig());
//            try {
//                JsonFormat.merge(jsonFormat, tableBuilder);
//                builder.addResult(tableBuilder);
//            } catch (JsonFormat.ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return builder.build();
//    }
//
//    public static String printToString(Message message) {
//        return JsonFormat.printToString(message);
//    }
//
//    public static String printAppMessageToString(byte[] data) throws InvalidProtocolBufferException {
//        try {
//            BackendAppSendMessage message = BackendAppSendMessage.parseFrom(data);
//            return printToString(message);
//        } catch (InvalidProtocolBufferException e) {
//            BackendErrorMessage message = BackendErrorMessage.parseFrom(data);
//            return printToString(message);
//        }
//    }
//
//    public static String printFacesetMessageToString(byte[] data) throws InvalidProtocolBufferException {
//        try {
//            BackendFacesetSendMessage message = BackendFacesetSendMessage.parseFrom(data);
//            return printToString(message);
//        } catch (InvalidProtocolBufferException e) {
//            BackendErrorMessage message = BackendErrorMessage.parseFrom(data);
//            return printToString(message);
//        }
//    }
//
//    public static String printLoginMessageToString(byte[] data) throws InvalidProtocolBufferException {
//        try {
//            BackendLoginSendMessage message = BackendLoginSendMessage.parseFrom(data);
//            return printToString(message);
//        } catch (InvalidProtocolBufferException e) {
//            BackendErrorMessage message = BackendErrorMessage.parseFrom(data);
//            return printToString(message);
//        }
//    }
//}